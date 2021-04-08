package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageField implements MessageElement {

    public static MessageField of(String name) {
        return of(Locale.ENGLISH, name);
    }

    public static MessageField of(Locale locale, String name) {

        String[] parts = name.split("\\.");
        MessageField field = null;

        for (int i = parts.length - 1; i >= 0; i--) {
            field = new MessageField(locale, parts[i], field);
        }

        if (field != null) { // load caches
            MessageField lastSub = field.getLastSub();
            String lastSubPath = field.getLastSubPath();
        }

        return field;
    }

    private final Locale locale;
    private final String name;
    private final MessageField sub;
    private String defaultValue;
    private String metadataRaw;

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
        // load caches
        String[] metadataPlurals = this.getMetadataOptions();
    }

    public String[] getMetadataOptions() {
        if (this.metadataRaw == null) {
            return null;
        }
        if (this.metadataOptions == null) {
            this.metadataOptions = this.metadataRaw.split(",|;");
        }
        return this.metadataOptions;
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
}
