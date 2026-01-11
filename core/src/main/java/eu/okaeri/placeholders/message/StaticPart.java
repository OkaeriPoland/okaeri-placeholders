package eu.okaeri.placeholders.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StaticPart implements MessageElement {

    private String value;

    public static StaticPart of(@NonNull String value) {
        return new StaticPart(value);
    }
}
