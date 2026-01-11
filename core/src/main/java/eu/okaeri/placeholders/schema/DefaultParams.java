package eu.okaeri.placeholders.schema;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.FieldParams;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

/**
 * Default implementation of {@link Params} that wraps a FieldParams instance.
 */
@RequiredArgsConstructor
public class DefaultParams implements Params {

    private final FieldParams fieldParams;
    private final Locale locale;
    private final PlaceholderContext context;

    /**
     * Creates a Params wrapper around FieldParams.
     */
    public static Params of(FieldParams fieldParams, Locale locale, PlaceholderContext context) {
        return new DefaultParams(fieldParams, locale, context);
    }

    @Override
    public Arg arg(int index) {
        return new DefaultArg(this.fieldParams, index, this.locale, this.context);
    }

    @Override
    public int length() {
        return this.fieldParams.length();
    }

    @Override
    public String[] toStringArray() {
        return this.fieldParams.strArr();
    }
}
