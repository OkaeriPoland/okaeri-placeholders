package eu.okaeri.placeholders.ast.parser;

import eu.okaeri.placeholders.ast.SourceSpan;
import lombok.NonNull;
import lombok.Value;

/**
 * A token produced by the lexer.
 */
@Value
public class Token {

    @NonNull TokenType type;
    String value;
    SourceSpan span;

    public static Token of(TokenType type, int pos) {
        return new Token(type, null, SourceSpan.at(pos));
    }

    public static Token of(TokenType type, String value, int start, int end) {
        return new Token(type, value, SourceSpan.of(start, end));
    }

    public static Token of(TokenType type, String value, SourceSpan span) {
        return new Token(type, value, span);
    }

    public static Token eof(int pos) {
        return new Token(TokenType.EOF, null, SourceSpan.at(pos));
    }

    @Override
    public String toString() {
        if (this.value != null) {
            return this.type + "(" + this.value + ")";
        }
        return this.type.toString();
    }
}
