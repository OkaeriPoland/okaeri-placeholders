package eu.okaeri.placeholders.ast.node;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A reference to a root identifier (e.g., player, $, active).
 * <p>
 * This is the starting point of an expression chain.
 * Examples: {player}, {$}, {name}
 * <p>
 * The raw {@code literal} field, when set, is the source text of this reference
 * including any surrounding whitespace (e.g. {@code " name "} for {@code f( name )}).
 * Use {@code getLiteral()} to always get a non-null source representation: it falls
 * back to the trimmed identifier name when no explicit literal was captured. Arg-evaluation
 * uses this for fallback when value lookup misses — so {@code prepend( wrap )}
 * preserves spaces when no {@code wrap} is bound while {@code default( name , "Guest" )}
 * still resolves {@code name} to its value.
 */
@Value
public class Ref implements AstNode {

    @NonNull String name;
    SourceSpan sourceSpan;
    @Getter(AccessLevel.NONE) @Nullable String literal;

    /**
     * Source representation of this reference — the raw literal if captured at parse
     * time (with any surrounding whitespace), otherwise the trimmed identifier name.
     * Always non-null; suitable for fallback rendering or programmatic introspection.
     */
    public String getLiteral() {
        return this.literal != null ? this.literal : this.name;
    }

    public static Ref of(String name) {
        return new Ref(name, null, null);
    }

    public static Ref of(String name, SourceSpan span) {
        return new Ref(name, span, null);
    }

    public static Ref of(String name, SourceSpan span, @Nullable String literal) {
        return new Ref(name, span, literal);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitRef(this);
    }

    @Override
    public List<AstNode> children() {
        return Collections.emptyList();
    }

    @Override
    public SourceSpan sourceSpan() {
        return this.sourceSpan;
    }

    @Override
    public String toString() {
        return "Ref(" + this.name + ")";
    }
}
