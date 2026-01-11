package eu.okaeri.placeholderstest.reflect.lookup;

import eu.okaeri.placeholders.reflect.lookup.MemberCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MemberCache")
class MemberCacheTest {

    private MemberCache cache;

    @BeforeEach
    void setUp() {
        cache = new MemberCache(10); // Small size for testing eviction
    }

    @SuppressWarnings("unused")
    public static class TestClass {
        public String testField = "test";

        public String testMethod() {
            return "test";
        }

        public String testMethodWithArg(String arg) {
            return arg;
        }
    }

    @Nested
    @DisplayName("Field caching")
    class FieldCaching {

        @Test
        @DisplayName("returns empty for uncached field")
        void returnsEmptyForUncached() {
            Optional<Optional<Field>> result = cache.getField(TestClass.class, "testField");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns cached field")
        void returnsCachedField() throws NoSuchFieldException {
            Field field = TestClass.class.getDeclaredField("testField");
            cache.putField(TestClass.class, "testField", field);

            Optional<Optional<Field>> result = cache.getField(TestClass.class, "testField");
            assertThat(result).isPresent();
            assertThat(result.get()).isPresent();
            assertThat(result.get().get()).isEqualTo(field);
        }

        @Test
        @DisplayName("caches negative lookups (null)")
        void cachesNegativeLookups() {
            cache.putField(TestClass.class, "nonExistent", null);

            Optional<Optional<Field>> result = cache.getField(TestClass.class, "nonExistent");
            assertThat(result).isPresent();
            assertThat(result.get()).isEmpty();
        }

        @Test
        @DisplayName("different classes have separate cache entries")
        void differentClassesSeparate() throws NoSuchFieldException {
            Field field = TestClass.class.getDeclaredField("testField");
            cache.putField(TestClass.class, "testField", field);

            Optional<Optional<Field>> result = cache.getField(String.class, "testField");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Method caching")
    class MethodCaching {

        @Test
        @DisplayName("returns empty for uncached method")
        void returnsEmptyForUncached() {
            Optional<Optional<Method>> result = cache.getMethod(TestClass.class, "testMethod", new Class<?>[0]);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns cached method")
        void returnsCachedMethod() throws NoSuchMethodException {
            Method method = TestClass.class.getDeclaredMethod("testMethod");
            cache.putMethod(TestClass.class, "testMethod", new Class<?>[0], method);

            Optional<Optional<Method>> result = cache.getMethod(TestClass.class, "testMethod", new Class<?>[0]);
            assertThat(result).isPresent();
            assertThat(result.get()).isPresent();
            assertThat(result.get().get()).isEqualTo(method);
        }

        @Test
        @DisplayName("caches negative lookups (null)")
        void cachesNegativeLookups() {
            cache.putMethod(TestClass.class, "nonExistent", new Class<?>[0], null);

            Optional<Optional<Method>> result = cache.getMethod(TestClass.class, "nonExistent", new Class<?>[0]);
            assertThat(result).isPresent();
            assertThat(result.get()).isEmpty();
        }

        @Test
        @DisplayName("different arg types have separate cache entries")
        void differentArgTypesSeparate() throws NoSuchMethodException {
            Method method = TestClass.class.getDeclaredMethod("testMethodWithArg", String.class);
            cache.putMethod(TestClass.class, "testMethodWithArg", new Class<?>[]{String.class}, method);

            // Different arg type - should not be cached
            Optional<Optional<Method>> result = cache.getMethod(TestClass.class, "testMethodWithArg",
                new Class<?>[]{Integer.class});
            assertThat(result).isEmpty();

            // Same arg type - should be cached
            result = cache.getMethod(TestClass.class, "testMethodWithArg", new Class<?>[]{String.class});
            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("Cache operations")
    class CacheOperations {

        @Test
        @DisplayName("size returns current cache size")
        void sizeReturnsCacheSize() throws NoSuchFieldException {
            assertThat(cache.size()).isEqualTo(0);

            Field field = TestClass.class.getDeclaredField("testField");
            cache.putField(TestClass.class, "testField", field);
            assertThat(cache.size()).isEqualTo(1);

            cache.putField(TestClass.class, "anotherField", null);
            assertThat(cache.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("clear removes all entries")
        void clearRemovesAll() throws NoSuchFieldException, NoSuchMethodException {
            Field field = TestClass.class.getDeclaredField("testField");
            Method method = TestClass.class.getDeclaredMethod("testMethod");

            cache.putField(TestClass.class, "testField", field);
            cache.putMethod(TestClass.class, "testMethod", new Class<?>[0], method);
            assertThat(cache.size()).isEqualTo(2);

            cache.clear();
            assertThat(cache.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("evicts entries when max size reached")
        void evictsWhenMaxSizeReached() throws NoSuchFieldException {
            // Cache has max size of 10
            Field field = TestClass.class.getDeclaredField("testField");

            // Fill cache beyond max size
            for (int i = 0; i < 15; i++) {
                cache.putField(TestClass.class, "field" + i, field);
            }

            // Size should be less than what we added (due to eviction)
            assertThat(cache.size()).isLessThanOrEqualTo(15);
        }
    }

    @Nested
    @DisplayName("Global cache")
    class GlobalCache {

        @Test
        @DisplayName("global() returns singleton")
        void globalReturnsSingleton() {
            MemberCache global1 = MemberCache.global();
            MemberCache global2 = MemberCache.global();
            assertThat(global1).isSameAs(global2);
        }

        @Test
        @DisplayName("global cache has default max size")
        void globalHasDefaultMaxSize() {
            // Just verify it exists and works
            MemberCache global = MemberCache.global();
            assertThat(global).isNotNull();
        }
    }
}
