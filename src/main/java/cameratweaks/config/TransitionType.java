package cameratweaks.config;

import java.util.function.Function;

import static java.lang.StrictMath.*;
import static net.minecraft.util.Mth.PI;
import static net.minecraft.util.Mth.cos;
import static net.minecraft.util.Mth.sin;
import static net.minecraft.util.Mth.sqrt;

// Based on https://easings.net
public enum TransitionType {
    LINEAR(x -> x),

    SINE_IN(x -> 1F - cos(x * PI / 2F), x -> (float) acos(-x + 1.0) * 2F / PI),
    SINE_OUT(x -> sin(x * PI / 2F), x -> (float) asin(x) * 2F / PI),
    SINE_IN_OUT(x -> 0.5F - cos(PI * x) / 2F),

    QUAD_IN(2),
    QUAD_OUT(2),
    QUAD_IN_OUT(2),

    CUBIC_IN(3),
    CUBIC_OUT(3),
    CUBIC_IN_OUT(3),

    QUART_IN(4),
    QUART_OUT(4),
    QUART_IN_OUT(4),

    QUINT_IN(5),
    QUINT_OUT(5),
    QUINT_IN_OUT(5),

    EXPO_IN(x -> (float) pow(2.0, 10.0 * x - 10.0), x -> 1F + (float) log(x) / ((float) log(2F) / 10F)),
    EXPO_OUT(x -> 1F - (float) pow(2.0, -10.0 * x), x -> (float) -log(1.0 - x) / ((float) log(2F) / 10F)),
    EXPO_IN_OUT(x -> x < 0.5F ? (float) pow(2, 20 * x - 10) / 2F : (2F - (float) pow(2, -20 * x + 10)) / 2F),

    CIRC_IN(x-> 1F - sqrt(1F - x * x), x-> 1F - sqrt(1F - (1F - x) * (1F - x))),
    CIRC_OUT(x-> sqrt(1F - (x - 1F) * (x - 1F)), x-> sqrt(1F - x * x)),
    CIRC_IN_OUT(x-> x < 0.5F ? (1F - sqrt(1F - (float) pow(2.0 * x, 2.0))) / 2F : (sqrt(1F - (float) pow(-2.0 * x + 2.0, 2.0)) + 1F) / 2F);

    private final Function<Float, Float> function;
    private final Function<Float, Float> inverse;

    TransitionType(int i) {
        if(name().endsWith("_IN")) {
            this.function = x -> (float) pow(x, i);
            this.inverse = x -> 1F - (float) pow(1.0 - x, i);
        } else if(name().endsWith("_IN_OUT")) {
            float mul = (float) pow(2.0, i - 1.0);
            this.function = x -> x < 0.5F? mul * (float) pow(x, i) : 1F - (float) pow(-2.0 * x + 2.0, x) / 2F;
            this.inverse = null;
        } else {
            this.function = x -> 1F - (float) pow(1.0 - x, i);
            this.inverse = x -> 1F - (float) pow(1.0 - x, 1.0 / i);
        }
    }

    TransitionType(Function<Float, Float> function) {
        this(function, null);
    }

    TransitionType(Function<Float, Float> function, Function<Float, Float> inverse) {
        this.function = function;
        this.inverse = inverse;
    }

    public float apply(float x) {
        return x <= 0F ? 0F : x >= 1F ? 1F : function.apply(x);
    }

    public float inverse(float x) {
        return x <= 0F ? 0F : x >= 1F ? 1F : inverse.apply(opposite().apply(x));
    }

    public boolean hasInverse() {
        return inverse != null;
    }

    public TransitionType opposite() {
        if (name().endsWith("_IN")) return values()[ordinal() + 1];
        if (name().endsWith("_OUT") && !name().endsWith("_IN_OUT")) return values()[ordinal() - 1];
        return this;
    }
}
