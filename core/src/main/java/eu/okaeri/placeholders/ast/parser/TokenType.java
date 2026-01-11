package eu.okaeri.placeholders.ast.parser;

/**
 * Token types for the placeholder expression lexer.
 * <p>
 * Design: Minimal token set. Only structural tokens and quoted strings
 * are distinguished. Everything else (numbers, operators, unicode, emoji)
 * is an IDENTIFIER, allowing maximum flexibility in field names.
 */
public enum TokenType {

    // Quoted string literal
    STRING,      // "hello", 'world'

    // Identifier (everything unquoted: field names, numbers, operators, emoji, etc.)
    IDENTIFIER,  // player, 123, -, emoji_🎉, etc.

    // Structural delimiters
    DOT,         // .
    LPAREN,      // (
    RPAREN,      // )
    COMMA,       // ,
    PIPE,        // | (default value separator)

    // End of input
    EOF
}
