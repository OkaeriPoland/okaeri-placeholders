package eu.okaeri.placeholders.ast.parser;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.node.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Recursive descent parser for placeholder expressions.
 * <p>
 * Grammar:
 * <pre>
 * expression = postfix ( "|" postfix )? ;
 * postfix    = primary ( "." identifier ( "(" arguments? ")" )? )* ;
 * primary    = STRING | identifier | "(" expression ")" ;
 * arguments  = expression ( "," expression )* ;
 * </pre>
 * <p>
 * Numbers are treated as identifiers. At evaluation time, if a "123" identifier
 * is not found as a field, it falls back to being used as a literal string,
 * which can then be parsed as a number by intAt()/doubleAt().
 */
public class ExpressionParser {

    private final String source;
    private final List<Token> tokens;
    private int current = 0;

    public ExpressionParser(String source) {
        this.source = source;
        this.tokens = new Lexer(source).tokenize();
    }

    public ExpressionParser(List<Token> tokens) {
        this.source = null;
        this.tokens = tokens;
    }

    /**
     * Parses the expression and returns the AST.
     */
    public AstNode parse() {
        if (tokens.isEmpty() || (tokens.size() == 1 && check(TokenType.EOF))) {
            // Empty expression - return empty string literal
            return StringLiteral.of("");
        }

        AstNode expr = parseExpression();

        if (!check(TokenType.EOF)) {
            throw error("Unexpected token after expression: " + peek());
        }

        return expr;
    }

    /**
     * expression = postfix ( "|" postfix )? ;
     */
    private AstNode parseExpression() {
        AstNode expr = parsePostfix();

        // Handle default value: {expr|default}
        if (match(TokenType.PIPE)) {
            AstNode defaultValue = parsePostfix();
            SourceSpan span = expr.sourceSpan() != null
                ? expr.sourceSpan().merge(defaultValue.sourceSpan())
                : null;
            return WithDefault.of(expr, defaultValue, span);
        }

        return expr;
    }

    /**
     * postfix = primary ( "." identifier ( "(" arguments? ")" )? )* ;
     * Also handles bare function calls: identifier(args) → $.identifier(args)
     */
    private AstNode parsePostfix() {
        AstNode expr = parsePrimary();

        // Handle bare function call: now() → $.now()
        // If we have a Ref directly followed by (, treat it as a global function
        if (expr instanceof Ref && check(TokenType.LPAREN)) {
            Ref ref = (Ref) expr;
            String name = ref.getName();
            advance(); // consume (
            List<AstNode> args = parseArguments();
            consume(TokenType.RPAREN, "Expected ')' after arguments");

            SourceSpan span = ref.sourceSpan() != null
                ? ref.sourceSpan().merge(previous().getSpan())
                : null;
            // Transform to $.name(args) - global function call
            Ref globalRef = Ref.of("$", ref.sourceSpan());
            expr = Call.of(globalRef, name, args, span);
        }

        while (match(TokenType.DOT)) {
            Token nameToken = consume(TokenType.IDENTIFIER, "Expected identifier after '.'");
            String name = nameToken.getValue();

            List<AstNode> args;
            if (match(TokenType.LPAREN)) {
                args = parseArguments();
                consume(TokenType.RPAREN, "Expected ')' after arguments");
            } else {
                args = Collections.emptyList();
            }

            SourceSpan span = expr.sourceSpan() != null
                ? expr.sourceSpan().merge(previous().getSpan())
                : null;
            expr = Call.of(expr, name, args, span);
        }

        return expr;
    }

    /**
     * primary = STRING | identifier | "(" expression ")" ;
     */
    private AstNode parsePrimary() {
        // String literal (quoted)
        if (match(TokenType.STRING)) {
            Token token = previous();
            return StringLiteral.of(token.getValue(), token.getSpan());
        }

        // Identifier (includes numbers, operators, emoji - anything unquoted)
        if (match(TokenType.IDENTIFIER)) {
            Token token = previous();
            return Ref.of(token.getValue(), token.getSpan());
        }

        // Grouped expression
        if (match(TokenType.LPAREN)) {
            AstNode expr = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return expr;
        }

        throw error("Expected expression, got: " + peek());
    }

    /**
     * arguments = expression ( "," expression )* ;
     * Supports empty arguments and special tokens as literals for backward compat.
     */
    private List<AstNode> parseArguments() {
        List<AstNode> args = new ArrayList<>();

        // Empty args: ()
        if (check(TokenType.RPAREN)) {
            return args;
        }

        // First argument
        args.add(parseArgumentExpression());

        // Additional arguments
        while (match(TokenType.COMMA)) {
            args.add(parseArgumentExpression());
        }

        return args;
    }

    /**
     * Parses an argument expression with lenient handling for backward compat.
     * - Empty argument (immediately followed by , or )) becomes empty string
     * - Whitespace-only argument (space between delimiter and , or )) preserved
     * - DOT token becomes "." string literal
     * - PIPE token becomes "|" string literal
     */
    private AstNode parseArgumentExpression() {
        // Empty argument: ,) or ,,
        // But first check if there's whitespace in the source that was skipped
        if (check(TokenType.RPAREN) || check(TokenType.COMMA)) {
            // Check if there's skipped whitespace between previous token and current
            if (source != null && current > 0) {
                Token prev = tokens.get(current - 1);
                Token curr = peek();
                int prevEnd = prev.getSpan().getEnd();
                int currStart = curr.getSpan().getStart();
                if (currStart > prevEnd) {
                    String skipped = source.substring(prevEnd, currStart);
                    // If there's whitespace that was skipped, use it as the argument
                    if (!skipped.isEmpty() && skipped.trim().isEmpty()) {
                        return StringLiteral.of(skipped, SourceSpan.of(prevEnd, currStart));
                    }
                }
            }
            return StringLiteral.of("", SourceSpan.of(peek().getSpan().getStart(), peek().getSpan().getStart()));
        }

        // DOT as literal string (for patterns like {s.replace(.,-)})
        if (check(TokenType.DOT)) {
            Token token = advance();
            return StringLiteral.of(".", token.getSpan());
        }

        // PIPE as literal string in arguments
        if (check(TokenType.PIPE)) {
            Token token = advance();
            return StringLiteral.of("|", token.getSpan());
        }

        return parseExpression();
    }

    // Helper methods

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return type == TokenType.EOF;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size() || peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        if (current >= tokens.size()) {
            return Token.eof(0);
        }
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message + ", got: " + peek());
    }

    private ParseException error(String message) {
        Token token = peek();
        int pos = token.getSpan() != null ? token.getSpan().getStart() : 0;
        return new ParseException(message, pos);
    }
}
