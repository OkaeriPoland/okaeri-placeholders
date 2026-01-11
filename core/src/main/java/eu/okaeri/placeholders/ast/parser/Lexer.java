package eu.okaeri.placeholders.ast.parser;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer for placeholder expressions.
 * <p>
 * Design: Minimal tokenization - only structural tokens are distinguished.
 * Everything else is an identifier. This allows any Unicode (including emoji),
 * numbers, operators, etc. in field names for maximum backward compatibility.
 * <p>
 * Structural tokens: . ( ) , | " '
 * Everything else: identifier (consumed until structural char or whitespace)
 */
@RequiredArgsConstructor
public class Lexer {

    private final String source;
    private int pos = 0;

    /**
     * Tokenizes the entire source string.
     *
     * @return list of tokens including EOF
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (!this.isAtEnd()) {
            Token token = this.nextToken();
            if (token != null) {
                tokens.add(token);
            }
        }
        tokens.add(Token.eof(this.pos));
        return tokens;
    }

    /**
     * Returns the next token, or null if whitespace was skipped.
     */
    private Token nextToken() {
        this.skipWhitespace();
        if (this.isAtEnd()) return null;

        int start = this.pos;
        char c = this.advance();

        // Structural tokens
        switch (c) {
            case '.':
                return Token.of(TokenType.DOT, start);
            case '(':
                return Token.of(TokenType.LPAREN, start);
            case ')':
                return Token.of(TokenType.RPAREN, start);
            case ',':
                return Token.of(TokenType.COMMA, start);
            case '|':
                return Token.of(TokenType.PIPE, start);
        }

        // String literals (quoted)
        if ((c == '"') || (c == '\'')) {
            return this.scanString(c, start);
        }

        // Everything else is an identifier - consume until structural or whitespace
        return this.scanIdentifier(start);
    }

    /**
     * Scans a string literal (quoted).
     */
    private Token scanString(char quote, int start) {
        StringBuilder sb = new StringBuilder();
        while (!this.isAtEnd() && (this.peek() != quote)) {
            char c = this.advance();
            if ((c == '\\') && !this.isAtEnd()) {
                char next = this.advance();
                switch (next) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\'':
                        sb.append('\'');
                        break;
                    default:
                        sb.append(next);
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        if (this.isAtEnd()) {
            throw this.error("Unterminated string", start);
        }
        this.advance(); // consume closing quote
        return Token.of(TokenType.STRING, sb.toString(), start, this.pos);
    }

    /**
     * Scans an identifier - everything until structural char or whitespace.
     * First char already consumed by caller.
     * <p>
     * Special handling for numbers: if identifier looks numeric (starts with digit or -digit),
     * we also consume a decimal point followed by digits (e.g., 2.5 stays as one token).
     */
    private Token scanIdentifier(int start) {
        char firstChar = this.source.charAt(start);
        boolean looksNumeric = this.isDigit(firstChar) ||
            ((firstChar == '-') && (this.pos < this.source.length()) && this.isDigit(this.source.charAt(this.pos)));

        while (!this.isAtEnd() && !this.isStructural(this.peek()) && !Character.isWhitespace(this.peek())) {
            this.advance();
        }

        // Special case: numeric identifier followed by . and more digits (decimal number)
        // e.g., "2.5" should be one identifier, not "2", ".", "5"
        if (looksNumeric && !this.isAtEnd() && (this.peek() == '.')) {
            int dotPos = this.pos;
            if (((dotPos + 1) < this.source.length()) && this.isDigit(this.source.charAt(dotPos + 1))) {
                this.advance(); // consume the dot
                while (!this.isAtEnd() && this.isDigit(this.peek())) {
                    this.advance();
                }
            }
        }

        String value = this.source.substring(start, this.pos);
        return Token.of(TokenType.IDENTIFIER, value, start, this.pos);
    }

    private boolean isDigit(char c) {
        return (c >= '0') && (c <= '9');
    }

    // Helper methods

    private boolean isStructural(char c) {
        return (c == '.') || (c == '(') || (c == ')') || (c == ',') || (c == '|') || (c == '"') || (c == '\'');
    }

    private void skipWhitespace() {
        while (!this.isAtEnd() && Character.isWhitespace(this.peek())) {
            this.advance();
        }
    }

    private char advance() {
        return this.source.charAt(this.pos++);
    }

    private char peek() {
        if (this.isAtEnd()) return '\0';
        return this.source.charAt(this.pos);
    }

    private boolean isAtEnd() {
        return this.pos >= this.source.length();
    }

    private ParseException error(String message, int pos) {
        return new ParseException(message, pos);
    }
}
