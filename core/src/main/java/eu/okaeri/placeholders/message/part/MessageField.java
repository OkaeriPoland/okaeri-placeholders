package eu.okaeri.placeholders.message.part;

import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

@Data
@EqualsAndHashCode(exclude = "raw")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageField implements MessageElement, MessageFieldAccessor {

    private static final MessageFieldTokenizer TOKENIZER = new MessageFieldTokenizer();

    private final Locale locale;
    private final String name;
    private final String source;
    private final @Nullable MessageField sub;

    private @Nullable String defaultValue;
    private @Nullable String metadataRaw;
    private @Nullable String raw;
    private @Getter FieldParams params;

    // cached values
    private String lastSubPath;
    private MessageField lastSub;
    private String[] metadataOptions;

    @Deprecated
    public static MessageField unknown() {
        return MessageField.of("unknown");
    }

    public static MessageField of(@NonNull String source) {
        return of(Locale.ENGLISH, source);
    }

    public static MessageField of(@NonNull Locale locale, @NonNull String source) {

        List<FieldParams> parts = TOKENIZER.tokenize(source);
        MessageField field = null;

        for (int i = parts.size() - 1; i >= 0; i--) {
            FieldParams pathElement = parts.get(i);
            field = new MessageField(locale, pathElement.getField(), source, field);
            field.setParams(pathElement);
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
        this.metadataOptions = TOKENIZER.tokenizeArgs(this.metadataRaw).toArray(new String[0]);
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
