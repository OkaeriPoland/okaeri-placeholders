package eu.okaeri.placeholders.message.part;

import java.util.Locale;

public interface MessageFieldAccessor {
    public Locale locale();
    public FieldParams params();
    public MessageField unsafe();
}
