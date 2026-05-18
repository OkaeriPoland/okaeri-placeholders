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

        // Bare-literal arg detection. An arg is captured verbatim as a string literal
        // (preserving ALL whitespace between the surrounding `(`/`,` and `,`/`)`) when:
        //   - it contains whitespace between tokens at depth 0 — multi-word phrases like
        //     `f(została zbanowana,...)` or `prepend(/cub extend <x> )` (with trailing
        //     space deliberate), OR
        //   - it contains a DOT at depth 0 not followed by IDENTIFIER, which can't parse
        //     as a method chain — e.g. `localize(Wł./Wył.)`.
        // STRING / PIPE inside an arg fall through to the normal expression parser
        // (string literals are explicit, `|` participates in default-value grammar).
        if ((this.source != null) && (this.check(TokenType.IDENTIFIER) || this.check(TokenType.DOT))) {
            int saved = this.current;
            int depth = 0;
            boolean hasWhitespaceGap = false;
            boolean hasTrailingDot = false;
            boolean disqualified = false;
            int prevTokenEnd = -1;
            int depth0IdentCount = 0;
            int depth0NonIdentCount = 0;
            int loneIdentTokenIdx = -1;
            int pos = saved;

            while (pos < this.tokens.size()) {
                Token tk = this.tokens.get(pos);
                TokenType tt = tk.getType();
                if (tt == TokenType.EOF) break;
                if ((depth == 0) && ((tt == TokenType.COMMA) || (tt == TokenType.RPAREN))) break;

                if (tt == TokenType.LPAREN) {
                    depth++;
                    // entering a nested call — the inner span is "consumed" as a unit,
                    // not whitespace, so reset the adjacency cursor
                    prevTokenEnd = -1;
                    depth0NonIdentCount++;
                    pos++;
                    continue;
                }
                if (tt == TokenType.RPAREN) {
                    depth--;
                    // returning from a nested call — treat the closer as the new adjacent
                    // boundary; whatever follows compares against this, not the pre-paren token
                    prevTokenEnd = tk.getSpan().getEnd();
                    pos++;
                    continue;
                }

                if (depth > 0) {
                    pos++;
                    continue;
                }

                if ((tt == TokenType.STRING) || (tt == TokenType.PIPE)) {
                    disqualified = true;
                    break;
                }
                int curStart = tk.getSpan().getStart();
                if ((prevTokenEnd >= 0) && (curStart > prevTokenEnd)) {
                    hasWhitespaceGap = true;
                }
                if (tt == TokenType.DOT) {
                    TokenType next = ((pos + 1) < this.tokens.size())
                        ? this.tokens.get(pos + 1).getType()
                        : TokenType.EOF;
                    if (next != TokenType.IDENTIFIER) {
                        hasTrailingDot = true;
                    }
                    depth0NonIdentCount++;
                } else if (tt == TokenType.IDENTIFIER) {
                    depth0IdentCount++;
                    if (loneIdentTokenIdx < 0) loneIdentTokenIdx = pos;
                } else {
                    depth0NonIdentCount++;
                }
                prevTokenEnd = tk.getSpan().getEnd();
                pos++;
            }

            if (!disqualified && (hasWhitespaceGap || hasTrailingDot)) {
                this.current = pos;
                int spanStart = this.tokens.get(saved - 1).getSpan().getEnd();
                int spanEnd = this.peek().getSpan().getStart();
                return StringLiteral.of(this.source.substring(spanStart, spanEnd), SourceSpan.of(spanStart, spanEnd));
            }

            // Single bare IDENT with edge whitespace — emit a Ref carrying the padded
            // source as the literal. At eval time the trimmed name is looked up first;
            // if no value is bound, the padded literal is used. Lets templates like
            // `default( name , "Guest" )` keep resolving `name` to its value while
            // `prepend( wrap )` (no `wrap` field) preserves " wrap " as the literal.
            if (!disqualified
                && (depth0IdentCount == 1)
                && (depth0NonIdentCount == 0)
                && (loneIdentTokenIdx >= 0)) {
                int spanStart = this.tokens.get(saved - 1).getSpan().getEnd();
                int spanEnd = this.tokens.get(pos).getSpan().getStart();
                Token identTok = this.tokens.get(loneIdentTokenIdx);
                if ((identTok.getSpan().getStart() > spanStart) || (identTok.getSpan().getEnd() < spanEnd)) {
                    this.current = pos;
                    String literal = this.source.substring(spanStart, spanEnd);
                    return Ref.of(identTok.getValue(), SourceSpan.of(spanStart, spanEnd), literal);
                }
            }
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
