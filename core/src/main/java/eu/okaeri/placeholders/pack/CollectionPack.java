package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Placeholder methods for {@link Collection} values.
 * <p>
 * Provides:
 * <ul>
 *   <li>Basics: {@code size} / {@code length}, {@code isEmpty}, {@code first}, {@code last}, {@code get(index)}, {@code contains(item)}</li>
 *   <li>Ordering: {@code reverse}, {@code sort}, {@code sortDesc}, {@code distinct}</li>
 *   <li>Slicing: {@code take(n)}, {@code drop(n)}</li>
 * </ul>
 * <p>
 * {@code sort} / {@code sortDesc} require elements to be {@link Comparable}; mixed-type
 * collections will throw {@link ClassCastException} from the underlying sort.
 * <p>
 * Ordering of {@code first}, {@code last}, and {@code get(index)} reflects the
 * collection's natural iteration order - well-defined for {@link List},
 * {@link java.util.LinkedHashSet}, and {@link java.util.SortedSet}, undefined
 * for {@link java.util.HashSet}.
 */
public class CollectionPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Collection.class)
            .add("size", Collection::size)
            .alias("size", "length")
            .add("isEmpty", Collection::isEmpty)

            .add("first", c -> c.isEmpty() ? null : c.iterator().next())
            .add("last", c -> lastOf(c))

            .add("get", (c, p) -> {
                int index = p.arg(0).asInt(-1);
                if ((index < 0) || (index >= c.size())) return null;
                if (c instanceof List) return ((List<?>) c).get(index);
                Iterator<?> it = c.iterator();
                Object current = null;
                for (int i = 0; i <= index; i++) current = it.next();
                return current;
            })

            .add("contains", (c, p, ctx) -> {
                Object needle = p.arg(0).resolve(ctx);
                if ((needle != null) && c.contains(needle)) return true;
                // Literal args resolve as null; fall back to string-equality
                String needleStr = p.arg(0).asString();
                if (needleStr.isEmpty()) return false;
                for (Object item : c) {
                    if (needleStr.equals(String.valueOf(item))) return true;
                }
                return false;
            })

            .add("reverse", c -> {
                List<Object> list = new ArrayList<>(c);
                Collections.reverse(list);
                return list;
            })

            .add("sort", c -> sortNatural(c))
            .add("sortDesc", c -> sortReverse(c))
            .add("distinct", c -> new ArrayList<>(new LinkedHashSet<>(c)))

            .add("take", (c, p) -> {
                int n = p.arg(0).asInt(0);
                if (n <= 0) return Collections.emptyList();
                if (n >= c.size()) return new ArrayList<>(c);
                List<Object> out = new ArrayList<>(n);
                Iterator<?> it = c.iterator();
                for (int i = 0; (i < n) && it.hasNext(); i++) out.add(it.next());
                return out;
            })

            .add("drop", (c, p) -> {
                int n = p.arg(0).asInt(0);
                if (n <= 0) return new ArrayList<>(c);
                if (n >= c.size()) return Collections.emptyList();
                List<Object> out = new ArrayList<>(c.size() - n);
                Iterator<?> it = c.iterator();
                for (int i = 0; i < n; i++) it.next();
                while (it.hasNext()) out.add(it.next());
                return out;
            });
    }

    private static Object lastOf(Collection<?> c) {
        if (c.isEmpty()) return null;
        if (c instanceof List) {
            List<?> list = (List<?>) c;
            return list.get(list.size() - 1);
        }
        Iterator<?> it = c.iterator();
        Object value = it.next();
        while (it.hasNext()) value = it.next();
        return value;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List sortNatural(Collection c) {
        List list = new ArrayList(c);
        Collections.sort(list);
        return list;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List sortReverse(Collection c) {
        List list = new ArrayList(c);
        list.sort(Collections.reverseOrder());
        return list;
    }
}
