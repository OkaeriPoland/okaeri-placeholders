package eu.okaeri.placeholders.schema.meta;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParameterMeta {

    public static ParameterMeta of(Class<?> type, String name) {
        return new ParameterMeta(type, name);
    }

    private final Class<?> type;
    private final String name;
}
