package eu.okaeri.placeholders.reflect;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;

public final class ReflectPlaceholders implements PlaceholderPack {

    public static Placeholders create() {
        return create(false);
    }

    public static Placeholders create(boolean registerDefaults) {
        return Placeholders.create(registerDefaults)
            .registerPlaceholders(new ReflectPlaceholders());
    }

    @Override
    public void register(Placeholders placeholders) {
        placeholders.fallbackResolver(new ReflectResolver());
    }
}
