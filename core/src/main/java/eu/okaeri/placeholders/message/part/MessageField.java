package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageField implements MessageElement {

    private static final Pattern PATH_ELEMENT_PATTERN = Pattern.compile("^(?<name>[^\\s(]+)(?:\\((?<params>.*)\\))?$");

    public static MessageField of(String name) {
        return of(Locale.ENGLISH, name);
    }

    public static MessageField of(Locale locale, String name) {

        String[] parts = name.split("\\.");
        MessageField field = null;

        for (int i = parts.length - 1; i >= 0; i--) {

            String pathElement = parts[i];
            Matcher matcher = PATH_ELEMENT_PATTERN.matcher(pathElement);

            if (!matcher.find()) {
                throw new RuntimeException("invalid field path element: " + pathElement);
            }

            String fieldRealName = matcher.group("name");
            String fieldParams = matcher.group("params");

            field = new MessageField(locale, fieldRealName, field);
            field.setParamsRaw(fieldParams);
        }

        if (field != null) { // load caches
            MessageField lastSub = field.getLastSub();
            String lastSubPath = field.getLastSubPath();
            FieldParams params = field.getParams();
        }

        return field;
    }

    private final Locale locale;
    private final String name;
    private final MessageField sub;
    private String defaultValue;
    private String metadataRaw;
    private String paramsRaw;

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        MessageField field = this;
        while (field.hasSub()) {
            MessageField sub = field.getSub();
            sub.setDefaultValue(defaultValue);
            field = sub;
        }
    }

    public boolean hasSub() {
        return this.sub != null;
    }

    public MessageField getLastSub() {
        if (this.sub == null) {
            return null;
        }
        if (this.lastSub == null) {
            MessageField last = this.sub;
            while (last.hasSub()) last = last.getSub();
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

    public void setMetadataRaw(String metadataRaw) {
        this.metadataRaw = metadataRaw;
        MessageField field = this;
        while (field.hasSub()) {
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
        this.metadataOptions = this.metadataRaw.split(",|;");
    }

    public FieldParams getParams() {
        if (this.paramsRaw == null) {
            this.params = FieldParams.empty();
        }
        if (this.params == null) {
            this.params = FieldParams.of(this.paramsRaw.split(",|;"));
        }
        return this.params;
    }

    private static String lastSubPath(MessageField field) {

        MessageField last = field;
        StringBuilder out = new StringBuilder(field.getName());

        while (last.hasSub()) {
            last = last.getSub();
            out.append(".").append(last.getName());
        }

        return out.toString();
    }

    // cached values
    private String lastSubPath;
    private MessageField lastSub;
    private String[] metadataOptions;
    private FieldParams params;
}
