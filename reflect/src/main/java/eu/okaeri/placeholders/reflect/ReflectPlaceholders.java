package eu.okaeri.placeholders.reflect;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;

public final class ReflectPlaceholders implements PlaceholderPack {

    /**
     * Create a Placeholders instance with defaults and reflection resolver.
     */
    public static Placeholders create() {
        return Placeholders.create().with(new ReflectPlaceholders());
    }

    /**
     * Create an empty Placeholders instance with only reflection resolver.
     */
    public static Placeholders empty() {
        return Placeholders.empty().with(new ReflectPlaceholders());
    }

    @Override
    public void register(Placeholders placeholders) {
        placeholders.fallback(new ReflectResolver());
    }
}
