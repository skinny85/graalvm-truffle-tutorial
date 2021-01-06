package com.endoflineblog.truffle.part_03;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
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

    /*
     * The @TypeCheck and @TypeCast methods are an alternative way to achieve what @ImplicitCast does.
     * Feel free to comment out castIntToDouble() above,
     * and uncomment the two methods below,
     * and see how does the generated code in AdditionNodeGen change!
     */

//    @TypeCheck(double.class)
//    public static boolean isDouble(Object value) {
//        return value instanceof Double || value instanceof Integer;
//    }
//
//    @TypeCast(double.class)
//    public static double asDouble(Object value) {
//        if (value instanceof Integer) {
//            return ((Integer) value).doubleValue();
//        } else {
//            return (double) value;
//        }
//    }
}
