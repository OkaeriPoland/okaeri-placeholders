package eu.okaeri.placeholders.adventure;

import eu.okaeri.placeholders.ast.EvaluationResult;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.MessageRenderer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
    private static final String OKAERI_TAG = "okaeri";

    private static final String LEGACY_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    private static final char LEGACY_MARKER = '\uE000';
    private static final Pattern LEGACY_MARKER_PATTERN = Pattern.compile(LEGACY_MARKER + "([" + LEGACY_CODES + "])");

    /**
     * Default MiniMessage that supports legacy color codes in template text.
     * <p>
     * Template &amp;-codes are converted to markers before parsing, then to §-codes after.
     * Runtime values containing &amp; are NOT parsed (safe from injection).
     */
    public static final MiniMessage DEFAULT_MINI_MESSAGE = MiniMessage.builder()
        .postProcessor(component -> component.replaceText(config -> config
            .match(Pattern.compile(".+", Pattern.DOTALL))
            .replacement((result, input) -> {
                String text = LEGACY_MARKER_PATTERN.matcher(result.group()).replaceAll("§$1");
                return LegacyComponentSerializer.legacySection().deserialize(text);
            })))
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
        Map<String, EvaluationResult> results = context.renderFieldResults(message);

        // Convert &/§ codes to markers in template text (outside placeholders)
        String withLegacy = this.convertLegacyCodes(raw);

        // Convert {name} → <okaeri:name> for MiniMessage integration
        String processed = this.convertPlaceholders(withLegacy);

        // Build tag resolver for okaeri placeholders (handles text content with style inheritance)
        TagResolver okaeriResolver = TagResolver.resolver(OKAERI_TAG, (args, ctx) -> {
            String fieldRaw = args.popOr("field name required").value();
            EvaluationResult result = results.get(fieldRaw);

            if (result == null) {
                // Field not in results - should not happen, but handle gracefully
                return Tag.inserting(Component.text("<unknown:" + fieldRaw + ">").color(NamedTextColor.RED));
            }

            return this.resultToTag(result);
        });

        // Parse with MiniMessage (handles text placeholders via TagResolver)
        Component component = this.baseMiniMessage.deserialize(processed, okaeriResolver);

        // Post-process: Replace placeholders in click/hover events (MiniMessage doesn't resolve tags in event values)
        Map<String, String> stringValues = results.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> this.resultToString(e.getValue())));
        return this.replaceEventsPlaceholders(component, stringValues);
    }

    /**
     * Converts an EvaluationResult to a MiniMessage Tag.
     */
    private Tag resultToTag(EvaluationResult result) {
        if (result instanceof EvaluationResult.Value) {
            EvaluationResult.Value valueResult = (EvaluationResult.Value) result;
            Object value = valueResult.getValue();

            if (value instanceof Component) {
                // Component values use their own styling (selfClosing preserves styles)
                return Tag.selfClosingInserting((Component) value);
            }
            if (value instanceof ComponentLike) {
                return Tag.selfClosingInserting(((ComponentLike) value).asComponent());
            }

            String text = String.valueOf(value);
            // Literals can have & codes processed (they're template-authored)
            // Non-literals (context values) are stripped of markers (safe from injection)
            if (valueResult.isLiteral()) {
                // Convert &/§ to § and deserialize as legacy
                String withMarkers = this.convertLegacyCodesAll(text);
                String withSection = LEGACY_MARKER_PATTERN.matcher(withMarkers).replaceAll("§$1");
                Component formatted = LegacyComponentSerializer.legacySection().deserialize(withSection);
                return Tag.inserting(formatted);
            } else {
                text = this.stripMarker(text);
                return Tag.inserting(Component.text(text));
            }
        } else if (result instanceof EvaluationResult.NullValue) {
            return Tag.inserting(Component.text("null").color(NamedTextColor.GRAY));
        } else if (result instanceof EvaluationResult.MissingValue) {
            String expr = result.getExpression();
            return Tag.inserting(Component.text("<missing:" + expr + ">").color(NamedTextColor.RED));
        }
        return Tag.inserting(Component.empty());
    }

    /**
     * Converts an EvaluationResult to a String (for event placeholders).
     */
    private String resultToString(EvaluationResult result) {
        if (result instanceof EvaluationResult.Value) {
            return String.valueOf(((EvaluationResult.Value) result).getValue());
        } else if (result instanceof EvaluationResult.NullValue) {
            return "null";
        } else if (result instanceof EvaluationResult.MissingValue) {
            return "<missing:" + result.getExpression() + ">";
        }
        return "";
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
     * Converts legacy &amp;-codes and §-codes to markers ONLY OUTSIDE placeholders.
     * Codes inside {...} are left untouched so expression keys remain unchanged.
     * The {@link #resultToTag} method handles conversion for literal values.
     */
    private String convertLegacyCodes(String raw) {
        StringBuilder result = new StringBuilder(raw.length());
        int depth = 0;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '{') {
                depth++;
                result.append(c);
            } else if (c == '}') {
                depth = Math.max(0, depth - 1);
                result.append(c);
            } else if ((depth == 0) && ((c == '&') || (c == '§')) && ((i + 1) < raw.length())) {
                char next = raw.charAt(i + 1);
                if (LEGACY_CODES.indexOf(next) >= 0) {
                    result.append(LEGACY_MARKER).append(next);
                    i++;
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts legacy codes to markers for use in literal values.
     */
    private String convertLegacyCodesAll(String raw) {
        StringBuilder result = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (((c == '&') || (c == '§')) && ((i + 1) < raw.length())) {
                char next = raw.charAt(i + 1);
                if (LEGACY_CODES.indexOf(next) >= 0) {
                    result.append(LEGACY_MARKER).append(next);
                    i++;
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Strips marker characters from non-literal values to prevent injection.
     */
    private String stripMarker(String text) {
        return text.replace(String.valueOf(LEGACY_MARKER), "");
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
