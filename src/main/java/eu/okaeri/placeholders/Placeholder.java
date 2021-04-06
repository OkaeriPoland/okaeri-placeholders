package eu.okaeri.placeholders;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholder {

    public static Placeholder of(Object value) {
        return new Placeholder(value);
    }

    private final Object value;

    public String render() {
        return String.valueOf(this.value);
    }
}
