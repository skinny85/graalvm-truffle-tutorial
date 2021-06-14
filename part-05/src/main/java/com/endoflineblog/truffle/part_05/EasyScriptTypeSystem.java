package com.endoflineblog.truffle.part_05;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * The TypeSystem for EasyScript,
 * the same as in part 3.
 */
@TypeSystem
public abstract class EasyScriptTypeSystem {
    @ImplicitCast
    public static double castIntToDouble(int value) {
        return value;
    }
}
