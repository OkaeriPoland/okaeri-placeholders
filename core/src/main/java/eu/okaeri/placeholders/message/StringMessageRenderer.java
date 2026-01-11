package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.bridge.PlaceholdersEvaluator;
import eu.okaeri.placeholders.ast.node.Call;
import eu.okaeri.placeholders.ast.node.Ref;
import eu.okaeri.placeholders.context.FailMode;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.ExpressionPart;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageStatic;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Default renderer that produces String output.
 * <p>
 * This is the standard renderer used by {@link PlaceholderContext#apply()}.
 */
public class StringMessageRenderer implements MessageRenderer<String> {

    public static final StringMessageRenderer INSTANCE = new StringMessageRenderer();

    @Override
    public String render(@NonNull CompiledMessage message, @NonNull PlaceholderContext context) {
        // Check for context/message mismatch
        CompiledMessage contextMessage = context.getMessage();
        if ((message != contextMessage) && (contextMessage != null)) {
            throw new IllegalArgumentException("cannot apply another message for context created with prepacked message: " +
                "if you intended to use this context as shared please use empty context from #create(), " +
                "if you're just trying to send a message use of(message)");
        }

        List<MessageElement> parts = message.getParts();
        FailMode failMode = context.getFailMode();
        Map<String, Object> values = context.getValues();
        PlaceholdersEvaluator evaluator = context.createEvaluator(message);

        StringBuilder builder = new StringBuilder();
        for (MessageElement part : parts) {
            if (part instanceof MessageStatic) {
                builder.append(((MessageStatic) part).getValue());
            } else if (part instanceof ExpressionPart) {
                ExpressionPart expr = (ExpressionPart) part;
                String result = evaluator.evaluateToString(expr.getAst());

                if (result == null) {
                    // Handle null result
                    if (expr.getDefaultValue() != null) {
                        result = expr.getDefaultValue();
                    } else if (failMode == FailMode.FAIL_FAST) {
                        // Determine if field is missing or null
                        String rootField = getRootFieldName(expr.getAst());
                        if (rootField != null && !values.containsKey(rootField)) {
                            throw new IllegalArgumentException("missing placeholder '" + rootField + "' for message '" + message.getRaw() + "'");
                        } else {
                            throw new IllegalArgumentException("resolved null for placeholder '{" + expr.getRaw() + "}' in message '" + message.getRaw() + "'");
                        }
                    } else if (failMode == FailMode.FAIL_SAFE) {
                        // Check if the root field is missing vs having null value
                        String rootField = getRootFieldName(expr.getAst());
                        if (rootField != null && !values.containsKey(rootField)) {
                            result = "<missing:" + expr.getRaw() + ">";
                        } else {
                            result = "<null:" + expr.getRaw() + ">";
                        }
                    } else {
                        throw new RuntimeException("unknown fail mode: " + failMode);
                    }
                }

                builder.append(result);
            } else {
                throw new IllegalArgumentException("unknown message part: " + part);
            }
        }

        return builder.toString();
    }

    /**
     * Extracts the root field name from an AST node.
     * For example: {player.name} → "player", {value} → "value"
     */
    private String getRootFieldName(AstNode node) {
        if (node instanceof Ref) {
            return ((Ref) node).getName();
        } else if (node instanceof Call) {
            return getRootFieldName(((Call) node).getTarget());
        }
        return null;
    }
}
