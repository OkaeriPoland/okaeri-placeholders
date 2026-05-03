package eu.okaeri.placeholders;

import eu.okaeri.placeholders.pack.*;
import eu.okaeri.placeholders.registry.Registry;

/**
 * Default placeholder pack that registers all built-in type methods and global functions.
 * <p>
 * This pack orchestrates registration of all sub-packs:
 * <ul>
 *   <li>{@link StringPack} - String manipulation</li>
 *   <li>{@link NumberPack} - Numeric operations and comparisons</li>
 *   <li>{@link ObjectPack} - Base object methods (equals, or, etc.)</li>
 *   <li>{@link BooleanPack} - Boolean formatting</li>
 *   <li>{@link EnumPack} - Enum operations</li>
 *   <li>{@link InstantPack} - Datetime formatting</li>
 *   <li>{@link DurationPack} - Duration formatting</li>
 *   <li>{@link MapPack} - Map operations</li>
 *   <li>{@link CollectionPack} - Collection operations</li>
 *   <li>{@link GlobalPack} - Global functions (if, coalesce, etc.)</li>
 * </ul>
 * <p>
 * Users can also register individual packs for finer control:
 * <pre>
 * Placeholders p = Placeholders.empty()
 *     .with(new StringPack())
 *     .with(new NumberPack())
 *     .with(new GlobalPack());
 * </pre>
 */
public class DefaultPlaceholderPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.packs(
            new StringPack(),
            new NumberPack(),
            new ObjectPack(),
            new BooleanPack(),
            new EnumPack(),
            new InstantPack(),
            new DurationPack(),
            new MapPack(),
            new CollectionPack(),
            new GlobalPack()
        );
    }
}
