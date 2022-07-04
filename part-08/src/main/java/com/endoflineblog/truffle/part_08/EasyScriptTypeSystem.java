package com.endoflineblog.truffle.part_08;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * The {@link TypeSystem} for EasyScript.
 * Almost identical to the class with the same name from part 7,
 * the only difference is that we add booleans to the hierarchy of EasyScript types.
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
