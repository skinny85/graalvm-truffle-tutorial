package com.endoflineblog.truffle.part_04;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * The TypeSystem for EasyScript,
 * where we have to specify that an `int` is also a valid `double`.
 */
@TypeSystem
public abstract class EasyScriptTypeSystem {
    /**
     * We tell the Truffle DSL that an `int` is always convertible to a `double`
     * in our language.
     */
    @ImplicitCast
    public static double castIntToDouble(int value) {
        return value;
    }
}
