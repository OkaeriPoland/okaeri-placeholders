package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.ast.EvaluationResult;
import eu.okaeri.placeholders.ast.bridge.PlaceholdersEvaluator;
import eu.okaeri.placeholders.context.FailMode;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.ExpressionPart;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageStatic;
import lombok.NonNull;

import java.util.List;

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
        PlaceholdersEvaluator evaluator = context.createEvaluator(message);

        StringBuilder builder = new StringBuilder();
        for (MessageElement part : parts) {
            if (part instanceof MessageStatic) {
                builder.append(((MessageStatic) part).getValue());
            } else if (part instanceof ExpressionPart) {
                ExpressionPart expr = (ExpressionPart) part;
                EvaluationResult result = evaluator.evaluateToResult(expr.getAst(), expr.getRaw());

                builder.append(this.formatResult(result, expr, message.getRaw(), failMode));
            } else {
                throw new IllegalArgumentException("unknown message part: " + part);
            }
        }

        return builder.toString();
    }

    /**
     * Formats an evaluation result for string output.
     */
    private String formatResult(EvaluationResult result, ExpressionPart expr, String messageRaw, FailMode failMode) {
        if (result instanceof EvaluationResult.Value) {
            Object value = ((EvaluationResult.Value) result).getValue();
            return this.objectToString(value);
        } else if (result instanceof EvaluationResult.NullValue) {
            // Check for default value
            if (expr.getDefaultValue() != null) {
                return expr.getDefaultValue();
            }
            if (failMode == FailMode.FAIL_FAST) {
                throw new IllegalArgumentException("resolved null for placeholder '{" + result.getExpression() + "}' in message '" + messageRaw + "'");
            }
            return "null";
        } else if (result instanceof EvaluationResult.MissingValue) {
            // Check for default value
            if (expr.getDefaultValue() != null) {
                return expr.getDefaultValue();
            }
            if (failMode == FailMode.FAIL_FAST) {
                throw new IllegalArgumentException("missing placeholder '" + ((EvaluationResult.MissingValue) result).getFieldName() + "' for message '" + messageRaw + "'");
            }
            return "<missing:" + result.getExpression() + ">";
        }
        throw new RuntimeException("unknown evaluation result type: " + result.getClass());
    }

    /**
     * Converts an object to string using the standard placeholder formatting.
     */
    private String objectToString(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof Enum) {
            return ((Enum<?>) object).name();
        }
        if ((object instanceof Float) || (object instanceof Double)) {
            return String.format("%.2f", object);
        }
        return object.toString();
    }
}
