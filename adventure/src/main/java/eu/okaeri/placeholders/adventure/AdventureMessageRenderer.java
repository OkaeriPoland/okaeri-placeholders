package eu.okaeri.placeholders.adventure;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.MessageRenderer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Renders placeholder messages as Adventure Components using MiniMessage.
 * <p>
 * This renderer converts okaeri-placeholders syntax ({name}) into MiniMessage tags
 * and uses TagResolver for value insertion. This approach preserves style inheritance
 * for string values (they inherit surrounding gradient, bold, etc.) while allowing
 * Component values to keep their own styling.
 * <p>
 * The default MiniMessage instance supports both legacy color codes (§ and &amp;) and
 * modern MiniMessage tags. Legacy codes are converted to &amp;-style first, then
 * processed alongside MiniMessage tags.
 * <p>
 * Example:
 * <pre>
 * // String values inherit surrounding styles
 * "&lt;gradient:red:blue&gt;Welcome {name}!&lt;/gradient&gt;"
 * // If name="World", "World" will have the gradient applied
 *
 * // Component values preserve their own styling
 * placeholders.registerPlaceholder(Player.class, "displayName", (p, a, ctx) ->
 *     Component.text(p.getName()).color(NamedTextColor.GOLD));
 * // The gold color is preserved, not overridden by surrounding styles
 *
 * // Mixed legacy and MiniMessage
 * "&amp;6Gold &lt;bold&gt;{name}&lt;/bold&gt;"
 * </pre>
 */
public class AdventureMessageRenderer implements MessageRenderer<Component> {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");
    private static final Pattern OKAERI_TAG_PATTERN = Pattern.compile("<okaeri:([^>]+)>");
    private static final Pattern LEGACY_SECTION_PATTERN = Pattern.compile("§([0-9A-Fa-fK-Ok-oRXrx])");
    private static final String OKAERI_TAG = "okaeri";

    /**
     * Default MiniMessage that supports both legacy color codes and MiniMessage tags.
     * <p>
     * Processing order:
     * <ol>
     *   <li>Pre-process: Convert §-codes to &amp;-codes</li>
     *   <li>Parse MiniMessage tags normally</li>
     *   <li>Post-process: Convert remaining &amp;-codes in text to styled components</li>
     * </ol>
     */
    public static final MiniMessage DEFAULT_MINI_MESSAGE = MiniMessage.builder()
        .preProcessor(text -> LEGACY_SECTION_PATTERN.matcher(text).replaceAll("&$1"))
        .postProcessor(component -> component.replaceText(config -> config
            .match(Pattern.compile(".++", Pattern.DOTALL))
            .replacement((result, input) -> LegacyComponentSerializer.legacyAmpersand().deserialize(result.group()))))
        .build();

    private final MiniMessage baseMiniMessage;

    /**
     * Creates a renderer with default MiniMessage that supports both legacy and modern syntax.
     */
    public AdventureMessageRenderer() {
        this(DEFAULT_MINI_MESSAGE);
    }

    /**
     * Creates a renderer with custom MiniMessage settings.
     *
     * @param baseMiniMessage The base MiniMessage instance to extend with okaeri resolver
     */
    public AdventureMessageRenderer(@NonNull MiniMessage baseMiniMessage) {
        this.baseMiniMessage = baseMiniMessage;
    }

    /**
     * Renders the message as a Component using MiniMessage parsing.
     * <p>
     * Placeholders are converted to MiniMessage tags, allowing string values
     * to inherit surrounding styles (gradient, bold, etc.) while Component
     * values preserve their own styling.
     *
     * @param message The compiled message to render
     * @param context The placeholder context with resolved values
     * @return The rendered Component
     */
    @Override
    public Component render(@NonNull CompiledMessage message, @NonNull PlaceholderContext context) {
        String raw = message.getRaw();
        Map<String, Object> values = context.renderFieldValues(message);

        // Convert {name} → <okaeri:name> for MiniMessage integration
        String processed = this.convertPlaceholders(raw);

        // Build tag resolver for okaeri placeholders (handles text content with style inheritance)
        TagResolver okaeriResolver = TagResolver.resolver(OKAERI_TAG, (args, ctx) -> {
            String fieldRaw = args.popOr("field name required").value();
            Object value = values.get(fieldRaw);

            if (value == null) {
                return Tag.inserting(Component.empty());
            }
            if (value instanceof Component) {
                // Component values use their own styling (selfClosing preserves styles)
                return Tag.selfClosingInserting((Component) value);
            }
            if (value instanceof ComponentLike) {
                return Tag.selfClosingInserting(((ComponentLike) value).asComponent());
            }
            // String values inherit surrounding styles (inserting allows style inheritance)
            return Tag.inserting(Component.text(String.valueOf(value)));
        });

        // Parse with MiniMessage (handles text placeholders via TagResolver)
        Component component = this.baseMiniMessage.deserialize(processed, okaeriResolver);

        // Post-process: Replace placeholders in click/hover events (MiniMessage doesn't resolve tags in event values)
        Map<String, String> stringValues = values.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
        return this.replaceEventsPlaceholders(component, stringValues);
    }

    /**
     * Converts okaeri placeholder syntax to MiniMessage tag syntax.
     * <pre>
     * {name} → &lt;okaeri:name&gt;
     * {player.name} → &lt;okaeri:player.name&gt;
     * {name.or("default")} → &lt;okaeri:name.or("default")&gt;
     * </pre>
     */
    private String convertPlaceholders(String raw) {
        return PLACEHOLDER_PATTERN.matcher(raw).replaceAll("<" + OKAERI_TAG + ":$1>");
    }

    /**
     * Recursively replaces placeholders in click and hover event values.
     * MiniMessage doesn't resolve tags in event attribute values, so we handle them here.
     */
    private Component replaceEventsPlaceholders(Component component, Map<String, String> values) {
        // Process children recursively
        component = component.children(component.children().stream()
            .map(child -> this.replaceEventsPlaceholders(child, values))
            .collect(Collectors.toList()));

        // Replace placeholders in click event
        ClickEvent clickEvent = component.clickEvent();
        if (clickEvent != null) {
            String newValue = this.replaceEventPlaceholders(clickEvent.value(), values);
            component = component.clickEvent(ClickEvent.clickEvent(clickEvent.action(), newValue));
        }

        // Replace placeholders in hover event (show_text type contains a Component, others contain strings)
        HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
            if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
                @SuppressWarnings("unchecked")
                HoverEvent<Component> textHover = (HoverEvent<Component>) hoverEvent;
                Component hoverValue = this.replaceEventsPlaceholders(textHover.value(), values);
                component = component.hoverEvent(HoverEvent.showText(hoverValue));
            }
        }

        return component;
    }

    /**
     * Replaces &lt;okaeri:field&gt; patterns in event values with their string values.
     * Event values contain the converted tag format since conversion happens before parsing.
     */
    private String replaceEventPlaceholders(String text, Map<String, String> values) {
        Matcher matcher = OKAERI_TAG_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String field = matcher.group(1);
            String replacement = values.getOrDefault(field, matcher.group());
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
