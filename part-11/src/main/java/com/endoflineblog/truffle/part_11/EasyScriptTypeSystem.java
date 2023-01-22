package com.endoflineblog.truffle.part_11;

import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * The {@link TypeSystem} for EasyScript.
 * Identical to the class with the same name from part 9.
 */
@TypeSystem({
        boolean.class,
        int.class,
        double.class,
})
public abstract class EasyScriptTypeSystem {
    @ImplicitCast
    public static double castIntToDouble(int value) {
        return value;
    }

    @ImplicitCast
    public static boolean interpretAsDouble(Object value) {
        // 'undefined' is falsy
        if (value == Undefined.INSTANCE) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        // a number is falsy when it's 0
        if (value instanceof Integer) {
            return (Integer) value != 0;
        }
        if (value instanceof Double) {
            return (Double) value != 0.0;
        }
        // all other values are truthy
        return true;
    }
}
