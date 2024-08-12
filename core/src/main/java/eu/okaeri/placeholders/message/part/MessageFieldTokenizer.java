package eu.okaeri.placeholders.message.part;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageFieldTokenizer {

    public List<FieldParams> tokenize(@NonNull String field) {

        List<FieldParams> tokens = new ArrayList<>();
        char[] charArray = field.toCharArray();
        StringBuilder buffer = new StringBuilder();
        boolean parsingArgs = false;
        String name = "";

        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];

            if ((c == '(') && !parsingArgs) {
                name = buffer.toString();
                buffer.setLength(0);
                parsingArgs = true;
                continue;
            }

            if ((c == ')') && ((charArrayLength == (i + 1)) || (charArray[i + 1] == '.')) && parsingArgs) {
                tokens.add(FieldParams.of(name, this.tokenizeArgs(buffer.toString()).toArray(new String[0])));
                buffer.setLength(0);
                parsingArgs = false;
                i++;
                continue;
            }

            if (parsingArgs) {
                buffer.append(c);
                continue;
            }

            if (((charArrayLength == (i + 1)) || (charArray[i + 1] == '.'))) {
                buffer.append(c);
                tokens.add(FieldParams.of(buffer.toString(), new String[0]));
                buffer.setLength(0);
                i++;
                continue;
            }

            buffer.append(c);
        }

        return tokens;
    }

    public List<String> tokenizeArgs(@NonNull String argText) {

        if (argText.isEmpty()) {
            return Collections.singletonList("");
        }

        List<String> args = new ArrayList<>();
        char[] charArray = argText.toCharArray();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];
            if (c == ',') {
                if ((i > 0) && (charArray[i - 1] == '\\')) {
                    buffer.setCharAt(buffer.length() - 1, c);
                    continue;
                }
                args.add(buffer.toString());
                buffer.setLength(0);
                continue;
            }
            if (charArrayLength == (i + 1)) {
                buffer.append(c);
                args.add(buffer.toString());
                buffer.setLength(0);
                continue;
            }
            buffer.append(c);
        }

        return args;
    }
}
