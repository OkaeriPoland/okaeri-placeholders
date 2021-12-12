package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldParams {

    private final String[] params;

    public static FieldParams of(@NonNull String[] params) {
        return new FieldParams(params);
    }

    public static FieldParams empty() {
        return new FieldParams(new String[]{});
    }

    public String[] strArr() {
        return this.params;
    }

    public int[] intArr() {
        int[] arr = new int[this.params.length];
        for (int i = 0; i < this.params.length; i++) {
            arr[i] = this.intAt(i, 0);
        }
        return arr;
    }

    public double[] doubleArr() {
        double[] arr = new double[this.params.length];
        for (int i = 0; i < this.params.length; i++) {
            arr[i] = this.doubleAt(i, 0);
        }
        return arr;
    }

    public String strAt(int pos) {
        return this.strAt(pos, "");
    }

    public String strAt(int pos, String def) {
        if (pos >= this.params.length) return def;
        return this.params[pos];
    }

    public double doubleAt(int pos) {
        return this.doubleAt(pos, 0);
    }

    public double doubleAt(int pos, double def) {
        String str = this.strAt(pos, String.valueOf(def));
        try {
            return new BigDecimal(str).doubleValue();
        }
        catch (NumberFormatException exception) {
            return def;
        }
    }

    public int intAt(int pos) {
        return this.intAt(pos, 0);
    }

    public int intAt(int pos, int def) {
        String str = this.strAt(pos, String.valueOf(def));
        try {
            return new BigDecimal(str).intValue();
        }
        catch (NumberFormatException exception) {
            return def;
        }
    }
}
