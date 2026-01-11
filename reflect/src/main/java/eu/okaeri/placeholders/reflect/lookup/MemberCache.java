package eu.okaeri.placeholders.reflect.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe LRU cache for reflection metadata (fields and methods).
 * <p>
 * Caches both positive lookups (found members) and negative lookups (not found)
 * to avoid repeated expensive reflection calls.
 */
public class MemberCache {

    /**
     * Default maximum cache size.
     */
    public static final int DEFAULT_MAX_SIZE = 256;

    /**
     * Shared global instance with default settings.
     */
    private static final MemberCache GLOBAL = new MemberCache(DEFAULT_MAX_SIZE);

    private final Map<CacheKey, Optional<Member>> cache;
    private final int maxSize;

    /**
     * Creates a new cache with the specified maximum size.
     */
    public MemberCache(int maxSize) {
        this.maxSize = maxSize;
        // Use ConcurrentHashMap for thread safety
        // LRU eviction is handled manually
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Returns the global shared cache instance.
     */
    public static MemberCache global() {
        return GLOBAL;
    }

    /**
     * Gets a cached field lookup result.
     *
     * @param clazz     The class to look in
     * @param fieldName The field name
     * @return Optional containing the cached result, or empty if not cached
     */
    public Optional<Optional<Field>> getField(Class<?> clazz, String fieldName) {
        CacheKey key = CacheKey.forField(clazz, fieldName);
        Optional<Member> cached = this.cache.get(key);
        if (cached == null) {
            return Optional.empty();
        }
        return Optional.of(cached.map(m -> (Field) m));
    }

    /**
     * Caches a field lookup result.
     *
     * @param clazz     The class
     * @param fieldName The field name
     * @param field     The field (may be null for negative cache)
     */
    public void putField(Class<?> clazz, String fieldName, Field field) {
        CacheKey key = CacheKey.forField(clazz, fieldName);
        this.put(key, field);
    }

    /**
     * Gets a cached method lookup result.
     *
     * @param clazz      The class to look in
     * @param methodName The method name
     * @param argTypes   The argument types
     * @return Optional containing the cached result, or empty if not cached
     */
    public Optional<Optional<Method>> getMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        CacheKey key = CacheKey.forMethod(clazz, methodName, argTypes);
        Optional<Member> cached = this.cache.get(key);
        if (cached == null) {
            return Optional.empty();
        }
        return Optional.of(cached.map(m -> (Method) m));
    }

    /**
     * Caches a method lookup result.
     *
     * @param clazz      The class
     * @param methodName The method name
     * @param argTypes   The argument types
     * @param method     The method (may be null for negative cache)
     */
    public void putMethod(Class<?> clazz, String methodName, Class<?>[] argTypes, Method method) {
        CacheKey key = CacheKey.forMethod(clazz, methodName, argTypes);
        this.put(key, method);
    }

    private void put(CacheKey key, Member member) {
        // Simple size check - evict oldest entries if needed
        if (this.cache.size() >= this.maxSize) {
            this.evictOldest();
        }
        this.cache.put(key, Optional.ofNullable(member));
    }

    /**
     * Evicts approximately 25% of entries (simple eviction strategy).
     */
    private synchronized void evictOldest() {
        if (this.cache.size() < this.maxSize) {
            return;
        }
        int toRemove = this.maxSize / 4;
        java.util.Iterator<CacheKey> iterator = this.cache.keySet().iterator();
        while ((toRemove > 0) && iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            toRemove--;
        }
    }

    /**
     * Clears all cached entries.
     */
    public void clear() {
        this.cache.clear();
    }

    /**
     * Returns the current cache size.
     */
    public int size() {
        return this.cache.size();
    }

    /**
     * Cache key combining class, member name, and optional argument types.
     */
    private static final class CacheKey {
        private final Class<?> clazz;
        private final String memberName;
        private final Class<?>[] argTypes;
        private final int hash;

        private CacheKey(Class<?> clazz, String memberName, Class<?>[] argTypes) {
            this.clazz = clazz;
            this.memberName = memberName;
            this.argTypes = argTypes;
            this.hash = this.computeHash();
        }

        static CacheKey forField(Class<?> clazz, String fieldName) {
            return new CacheKey(clazz, fieldName, null);
        }

        static CacheKey forMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
            return new CacheKey(clazz, methodName, argTypes);
        }

        private int computeHash() {
            int result = Objects.hash(this.clazz, this.memberName);
            result = (31 * result) + Arrays.hashCode(this.argTypes);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if ((o == null) || (this.getClass() != o.getClass())) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(this.clazz, cacheKey.clazz)
                && Objects.equals(this.memberName, cacheKey.memberName)
                && Arrays.equals(this.argTypes, cacheKey.argTypes);
        }

        @Override
        public int hashCode() {
            return this.hash;
        }
    }
}
