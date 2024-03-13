package eu.okaeri.placeholders.message.part;

import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(exclude = "raw")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageField implements MessageElement, MessageFieldAccessor {

    private static final Pattern PATH_ELEMENT_PATTERN = Pattern.compile("^(?<name>[^\\s(]+)(?:\\((?<params>.*)\\))?$");

    private final Locale locale;
    private final String name;
    private final String source;
    @Nullable private final MessageField sub;

    @Nullable private String defaultValue;
    @Nullable private String metadataRaw;
    @Nullable private String paramsRaw;
    @Nullable private String raw;

    // cached values
    private String lastSubPath;
    private MessageField lastSub;
    private String[] metadataOptions;
    private FieldParams params;

    @Deprecated
    public static MessageField unknown() {
        return MessageField.of("unknown");
    }

    public static MessageField of(@NonNull String source) {
        return of(Locale.ENGLISH, source);
    }

    public static MessageField of(@NonNull Locale locale, @NonNull String source) {

        String[] parts = source.split("\\.");
        MessageField field = null;

        for (int i = parts.length - 1; i >= 0; i--) {

            String pathElement = parts[i];
            Matcher matcher = PATH_ELEMENT_PATTERN.matcher(pathElement);

            if (!matcher.find()) {
                throw new RuntimeException("invalid field path element: " + pathElement);
            }

            String fieldRealName = matcher.group("name");
            String fieldParams = matcher.group("params");

            field = new MessageField(locale, fieldRealName, source, field);
            field.setParamsRaw(fieldParams);
        }

        if (field != null) { // load caches
            MessageField lastSub = field.getLastSub();
            String lastSubPath = field.getLastSubPath();
            FieldParams params = field.getParams();
        }

        return field;
    }

    private static String lastSubPath(@NonNull MessageField field) {

        MessageField last = field;
        StringBuilder out = new StringBuilder(field.getName());

        while (last.getSub() != null) {
            last = last.getSub();
            out.append(".").append(last.getName());
        }

        return out.toString();
    }

    public void setDefaultValue(@Nullable String defaultValue) {
        this.defaultValue = defaultValue;
        MessageField field = this;
        while (field.getSub() != null) {
            MessageField sub = field.getSub();
            sub.setDefaultValue(defaultValue);
            field = sub;
        }
    }

    public boolean hasSub() {
        return this.sub != null;
    }

    @Nullable
    public MessageField getLastSub() {
        if (this.sub == null) {
            return null;
        }
        if (this.lastSub == null) {
            MessageField last = this.sub;
            while (last.getSub() != null) last = last.getSub();
            this.lastSub = last;
        }
        return this.lastSub;
    }

    public String getLastSubPath() {
        if (this.lastSubPath == null) {
            this.lastSubPath = lastSubPath(this);
        }
        return this.lastSubPath;
    }

    public void setMetadataRaw(@Nullable String metadataRaw) {
        this.metadataRaw = metadataRaw;
        MessageField field = this;
        while (field.getSub() != null) {
            MessageField sub = field.getSub();
            sub.setMetadataRaw(metadataRaw);
            sub.updateMetadataOptionsCache();
            field = sub;
        }
        this.updateMetadataOptionsCache();
    }

    public void updateMetadataOptionsCache() {
        if (this.metadataRaw == null) {
            return;
        }
        this.metadataOptions = splitPartsWithEscape(this.metadataRaw);
    }

    public FieldParams getParams() {
        if (this.paramsRaw == null) {
            this.params = FieldParams.empty(this.name);
        }
        if (this.params == null) {
            this.params = FieldParams.of(this.name, splitPartsWithEscape(this.paramsRaw));
        }
        return this.params;
    }

    private static String[] splitPartsWithEscape(String text) {
        String[] options = text.split("(?<!\\\\)(?:;|,)");
        for (int i = 0; i < options.length; i++) {
            options[i] = options[i]
                .replace("\\,", ",")
                .replace("\\;", ";");
        }
        return options;
    }

    @Override
    public Locale locale() {
        return this.getLocale();
    }

    @Override
    public FieldParams params() {
        return this.getParams();
    }

    @Override
    public MessageField unsafe() {
        return this;
    }
}
