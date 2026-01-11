package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import lombok.*;
import org.jetbrains.annotations.Nullable;

/**
 * A holder for field values in a {@link PlaceholderContext}.
 * <p>
 * Supports lazy evaluation via {@link java.util.function.Supplier}:
 * <pre>{@code
 * context.with("expensive", () -> computeExpensiveValue());
 * }</pre>
 */
@Data
@ToString(exclude = "context")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldValue {

    private final Object value;
    private Placeholders placeholders;
    private PlaceholderContext context;

    public static FieldValue of(@Nullable Object value) {
        return new FieldValue(value);
    }

    public static FieldValue of(@Nullable Placeholders placeholders, @Nullable Object value) {
        return of(placeholders, value, null);
    }

    public static FieldValue of(@Nullable Placeholders placeholders, @Nullable Object value, @Nullable PlaceholderContext context) {
        FieldValue fieldValue = new FieldValue(value);
        fieldValue.setPlaceholders(placeholders);
        fieldValue.setContext(context);
        return fieldValue;
    }
}
