package eu.okaeri.placeholders.ast;

import lombok.Value;

/**
 * Represents a span in source text, used for error reporting.
 */
@Value
public class SourceSpan {

    int start;
    int end;

    /**
     * Creates a source span covering a single position.
     */
    public static SourceSpan at(int pos) {
        return new SourceSpan(pos, pos + 1);
    }

    /**
     * Creates a source span covering a range.
     */
    public static SourceSpan of(int start, int end) {
        return new SourceSpan(start, end);
    }

    /**
     * Returns the length of this span.
     */
    public int length() {
        return this.end - this.start;
    }

    /**
     * Returns a span that covers both this span and another.
     */
    public SourceSpan merge(SourceSpan other) {
        if (other == null) return this;
        return new SourceSpan(
            Math.min(this.start, other.start),
            Math.max(this.end, other.end)
        );
    }
}
