package eu.okaeri.placeholders.reflect.argument;

import java.util.regex.Pattern;

/**
 * Enum representing the types of arguments that can be parsed from placeholder syntax.
 * <p>
 * Detection order matters - more specific patterns are checked first:
 * <ol>
 *   <li>{@link #CHAR_LITERAL} - {@code c'x'} (before STRING_LITERAL)</li>
 *   <li>{@link #STRING_LITERAL} - {@code 'hello'}</li>
 *   <li>{@link #BOOLEAN} - {@code true}, {@code false}</li>
 *   <li>{@link #LONG} - {@code 123L} (before INTEGER)</li>
 *   <li>{@link #FLOAT} - {@code 12.34f} (before DOUBLE)</li>
 *   <li>{@link #DOUBLE} - {@code 12.34}</li>
 *   <li>{@link #BYTE} - {@code 12b} (before INTEGER)</li>
 *   <li>{@link #SHORT} - {@code 12s} (before INTEGER)</li>
 *   <li>{@link #INTEGER} - {@code 123}</li>
 *   <li>{@link #CONTEXT_REF} - field reference from context</li>
 *   <li>{@link #UNKNOWN} - fallback, treated as literal string</li>
 * </ol>
 */
public enum ArgumentType {

    /**
     * Character literal: {@code c'x'} where x is a single character.
     */
    CHAR_LITERAL(Pattern.compile("^c'.'$"), Character.class) {
        @Override
        public Object parse(String raw) {
            return raw.charAt(2);
        }
    },

    /**
     * String literal: {@code 'hello'} or {@code "hello"}.
     */
    STRING_LITERAL(Pattern.compile("^['\"].*['\"]$"), String.class) {
        @Override
        public Object parse(String raw) {
            return raw.substring(1, raw.length() - 1);
        }
    },

    /**
     * Boolean: {@code true} or {@code false}.
     */
    BOOLEAN(Pattern.compile("^(true|false)$"), Boolean.class) {
        @Override
        public Object parse(String raw) {
            return Boolean.parseBoolean(raw);
        }
    },

    /**
     * Long integer: {@code 123L} or {@code -456L}.
     */
    LONG(Pattern.compile("^-?\\d+[Ll]$"), Long.class) {
        @Override
        public Object parse(String raw) {
            return Long.parseLong(raw.substring(0, raw.length() - 1));
        }
    },

    /**
     * Float: {@code 12.34f} or {@code -56.78F}.
     */
    FLOAT(Pattern.compile("^-?\\d+\\.\\d+[fF]$"), Float.class) {
        @Override
        public Object parse(String raw) {
            return Float.parseFloat(raw.substring(0, raw.length() - 1));
        }
    },

    /**
     * Double: {@code 12.34} or {@code -56.78}.
     */
    DOUBLE(Pattern.compile("^-?\\d+\\.\\d+$"), Double.class) {
        @Override
        public Object parse(String raw) {
            return Double.parseDouble(raw);
        }
    },

    /**
     * Byte: {@code 12b} or {@code -34B}.
     */
    BYTE(Pattern.compile("^-?\\d+[bB]$"), Byte.class) {
        @Override
        public Object parse(String raw) {
            return Byte.parseByte(raw.substring(0, raw.length() - 1));
        }
    },

    /**
     * Short: {@code 12s} or {@code -34S}.
     */
    SHORT(Pattern.compile("^-?\\d+[sS]$"), Short.class) {
        @Override
        public Object parse(String raw) {
            return Short.parseShort(raw.substring(0, raw.length() - 1));
        }
    },

    /**
     * Integer: {@code 123} or {@code -456}.
     */
    INTEGER(Pattern.compile("^-?\\d+$"), Integer.class) {
        @Override
        public Object parse(String raw) {
            return Integer.parseInt(raw);
        }
    },

    /**
     * Reference to a context field (contains . or () indicating method chain).
     * Actual resolution happens at runtime via PlaceholderContext.
     */
    CONTEXT_REF(null, Object.class) {
        @Override
        public boolean matches(String raw) {
            // Matches if it looks like a field path or method call
            return raw.contains(".") || raw.contains("(");
        }

        @Override
        public Object parse(String raw) {
            // Context refs are resolved at runtime, return raw for now
            return raw;
        }
    },

    /**
     * Unknown type - fallback. The raw string is used as-is.
     * Can be a simple field reference or a literal string depending on context.
     */
    UNKNOWN(null, String.class) {
        @Override
        public boolean matches(String raw) {
            return true; // Matches everything as fallback
        }

        @Override
        public Object parse(String raw) {
            return raw;
        }
    };

    private final Pattern pattern;
    private final Class<?> javaType;

    ArgumentType(Pattern pattern, Class<?> javaType) {
        this.pattern = pattern;
        this.javaType = javaType;
    }

    /**
     * Returns the Java type that this argument type maps to.
     */
    public Class<?> getJavaType() {
        return this.javaType;
    }

    /**
     * Checks if the raw string matches this argument type's pattern.
     */
    public boolean matches(String raw) {
        return (this.pattern != null) && this.pattern.matcher(raw).matches();
    }

    /**
     * Parses the raw string into the appropriate Java object.
     *
     * @param raw The raw string argument
     * @return The parsed value
     */
    public abstract Object parse(String raw);

    /**
     * Detects the argument type from a raw string.
     * Checks types in priority order (most specific first).
     *
     * @param raw The raw string argument
     * @return The detected ArgumentType
     */
    public static ArgumentType detect(String raw) {
        if ((raw == null) || raw.isEmpty()) {
            return UNKNOWN;
        }

        // Check in priority order (most specific first)
        for (ArgumentType type : values()) {
            if ((type != UNKNOWN) && type.matches(raw)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
