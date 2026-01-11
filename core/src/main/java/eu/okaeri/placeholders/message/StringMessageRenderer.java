package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.ast.EvaluationResult;
import eu.okaeri.placeholders.ast.ExpressionEvaluator;
import eu.okaeri.placeholders.context.FailMode;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.exception.MissingFieldException;
import eu.okaeri.placeholders.exception.NullValueException;
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
        ExpressionEvaluator evaluator = context.createEvaluator(message);

        StringBuilder builder = new StringBuilder();
        for (MessageElement part : parts) {
            if (part instanceof StaticPart) {
                builder.append(((StaticPart) part).getValue());
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
        // Handle successful value
        if (result instanceof EvaluationResult.Value) {
            return result.format();
        }

        // Check for default value first (applies to both NullValue and MissingValue)
        if (expr.getDefaultValue() != null) {
            return expr.getDefaultValue();
        }

        // Handle failure cases based on fail mode
        if (failMode == FailMode.FAIL_FAST) {
            if (result instanceof EvaluationResult.NullValue) {
                throw new NullValueException(result.getExpression(), messageRaw);
            }
            if (result instanceof EvaluationResult.MissingValue) {
                EvaluationResult.MissingValue mv = (EvaluationResult.MissingValue) result;
                throw new MissingFieldException(mv.getFieldName(), mv.getExpression(), messageRaw);
            }
        }

        // FAIL_SAFE mode: use standard formatting
        return result.format();
    }
}
