package com.endoflineblog.truffle.part_15;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * The {@link TypeSystem} for EasyScript.
 * Identical to the class with the same name from part 14.
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
}
