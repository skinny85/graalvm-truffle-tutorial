package com.endoflineblog.truffle.part_06;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * The {@link TypeSystem} for EasyScript.
 * Basically identical to the TypeSystem we've been using since part 3,
 * the only difference is that we pass the hierarchy of our primitives in the {@link TypeSystem#value} attribute.
 * Because of that, the Truffle DSL will generate a class,
 * {@link EasyScriptTypeSystemGen}, with methods like {@link EasyScriptTypeSystemGen#expectInteger}
 * and {@link EasyScriptTypeSystemGen#expectDouble},
 * which we use in {@link com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode}.
 */
@TypeSystem({
        int.class,
        double.class,
})
public abstract class EasyScriptTypeSystem {
    @ImplicitCast
    public static double castIntToDouble(int value) {
        return value;
    }
}
