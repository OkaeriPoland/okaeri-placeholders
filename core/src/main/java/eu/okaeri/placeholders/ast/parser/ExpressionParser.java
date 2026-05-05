package eu.okaeri.placeholders.ast.parser;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.node.Call;
import eu.okaeri.placeholders.ast.node.Ref;
import eu.okaeri.placeholders.ast.node.StringLiteral;
import eu.okaeri.placeholders.ast.node.WithDefault;
import eu.okaeri.placeholders.exception.ParseException;

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
        if (this.tokens.isEmpty() || ((this.tokens.size() == 1) && this.check(TokenType.EOF))) {
            // Empty expression - return empty string literal
            return StringLiteral.of("");
        }

        AstNode expr = this.parseExpression();

        if (!this.check(TokenType.EOF)) {
            throw this.error("Unexpected token after expression: " + this.peek());
        }

        return expr;
    }

    /**
     * expression = postfix ( "|" postfix )? ;
     */
    private AstNode parseExpression() {
        AstNode expr = this.parsePostfix();

        // Handle default value: {expr|default}
        if (this.match(TokenType.PIPE)) {
            AstNode defaultValue = this.parsePostfix();
            SourceSpan span = (expr.sourceSpan() != null)
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
        AstNode expr = this.parsePrimary();

        // Handle bare function call: now() → $.now()
        // If we have a Ref directly followed by (, treat it as a global function
        if ((expr instanceof Ref) && this.check(TokenType.LPAREN)) {
            Ref ref = (Ref) expr;
            String name = ref.getName();
            this.advance(); // consume (
            List<AstNode> args = this.parseArguments();
            this.consume(TokenType.RPAREN, "Expected ')' after arguments");

            SourceSpan span = (ref.sourceSpan() != null)
                ? ref.sourceSpan().merge(this.previous().getSpan())
                : null;
            // Transform to $.name(args) - global function call (always has parens)
            Ref globalRef = Ref.of("$", ref.sourceSpan());
            expr = Call.of(globalRef, name, args, true, span);
        }

        while (this.match(TokenType.DOT)) {
            Token nameToken = this.consume(TokenType.IDENTIFIER, "Expected identifier after '.'");
            String name = nameToken.getValue();

            List<AstNode> args;
            boolean hasParens = false;
            if (this.match(TokenType.LPAREN)) {
                hasParens = true;
                args = this.parseArguments();
                this.consume(TokenType.RPAREN, "Expected ')' after arguments");
            } else {
                args = Collections.emptyList();
            }

            SourceSpan span = (expr.sourceSpan() != null)
                ? expr.sourceSpan().merge(this.previous().getSpan())
                : null;
            expr = Call.of(expr, name, args, hasParens, span);
        }

        return expr;
    }

    /**
     * primary = STRING | identifier | "(" expression ")" ;
     */
    private AstNode parsePrimary() {
        // String literal (quoted)
        if (this.match(TokenType.STRING)) {
            Token token = this.previous();
            return StringLiteral.of(token.getValue(), token.getSpan());
        }

        // Identifier (includes numbers, operators, emoji - anything unquoted)
        if (this.match(TokenType.IDENTIFIER)) {
            Token token = this.previous();
            return Ref.of(token.getValue(), token.getSpan());
        }

        // Grouped expression
        if (this.match(TokenType.LPAREN)) {
            AstNode expr = this.parseExpression();
            this.consume(TokenType.RPAREN, "Expected ')' after expression");
            return expr;
        }

        throw this.error("Expected expression, got: " + this.peek());
    }

    /**
     * arguments = expression ( "," expression )* ;
     * Supports empty arguments and special tokens as literals for backward compat.
     */
    private List<AstNode> parseArguments() {
        List<AstNode> args = new ArrayList<>();

        // Empty args: ()
        if (this.check(TokenType.RPAREN)) {
            return args;
        }

        // First argument
        args.add(this.parseArgumentExpression());

        // Additional arguments
        while (this.match(TokenType.COMMA)) {
            args.add(this.parseArgumentExpression());
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
        if (this.check(TokenType.RPAREN) || this.check(TokenType.COMMA)) {
            // Check if there's skipped whitespace between previous token and current
            if ((this.source != null) && (this.current > 0)) {
                Token prev = this.tokens.get(this.current - 1);
                Token curr = this.peek();
                int prevEnd = prev.getSpan().getEnd();
                int currStart = curr.getSpan().getStart();
                if (currStart > prevEnd) {
                    String skipped = this.source.substring(prevEnd, currStart);
                    // If there's whitespace that was skipped, use it as the argument
                    if (!skipped.isEmpty() && skipped.trim().isEmpty()) {
                        return StringLiteral.of(skipped, SourceSpan.of(prevEnd, currStart));
                    }
                }
            }
            return StringLiteral.of("", SourceSpan.of(this.peek().getSpan().getStart(), this.peek().getSpan().getStart()));
        }

        // DOT as literal string (for patterns like {s.replace(.,-)})
        if (this.check(TokenType.DOT)) {
            Token token = this.advance();
            return StringLiteral.of(".", token.getSpan());
        }

        // PIPE as literal string in arguments
        if (this.check(TokenType.PIPE)) {
            Token token = this.advance();
            return StringLiteral.of("|", token.getSpan());
        }

        // Multi-word bare identifiers up to , or ) become a single string literal.
        // Common in i18n templates like {r:player.f(została zbanowana,został zbanowany,...)}.
        if ((this.source != null) && this.check(TokenType.IDENTIFIER)) {
            int saved = this.current;
            int identCount = 0;
            while (this.check(TokenType.IDENTIFIER)) {
                identCount++;
                this.advance();
            }
            if ((identCount > 1) && (this.check(TokenType.COMMA) || this.check(TokenType.RPAREN))) {
                Token first = this.tokens.get(saved);
                Token last = this.tokens.get(this.current - 1);
                int start = first.getSpan().getStart();
                int end = last.getSpan().getEnd();
                return StringLiteral.of(this.source.substring(start, end), SourceSpan.of(start, end));
            }
            this.current = saved;
        }

        return this.parseExpression();
    }

    // Helper methods

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                this.advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (this.isAtEnd()) return type == TokenType.EOF;
        return this.peek().getType() == type;
    }

    private Token advance() {
        if (!this.isAtEnd()) this.current++;
        return this.previous();
    }

    private boolean isAtEnd() {
        return (this.current >= this.tokens.size()) || (this.peek().getType() == TokenType.EOF);
    }

    private Token peek() {
        if (this.current >= this.tokens.size()) {
            return Token.eof(0);
        }
        return this.tokens.get(this.current);
    }

    private Token previous() {
        return this.tokens.get(this.current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (this.check(type)) return this.advance();
        throw this.error(message + ", got: " + this.peek());
    }

    private ParseException error(String message) {
        Token token = this.peek();
        int pos = (token.getSpan() != null) ? token.getSpan().getStart() : 0;
        String found = (token.getValue() != null) ? token.getValue() : token.getType().name();
        return new ParseException(message, this.source, pos, found, null);
    }
}
