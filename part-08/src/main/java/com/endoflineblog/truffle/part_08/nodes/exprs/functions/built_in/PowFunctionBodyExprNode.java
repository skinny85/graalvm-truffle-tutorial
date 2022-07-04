package com.endoflineblog.truffle.part_08.nodes.exprs.functions.built_in;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * An expression Node that represents the implementation of the
 * {@code Math.pow()} JavaScript function.
 * Identical to the class with the same name from part 7.
 */
public abstract class PowFunctionBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization(guards = "exponent >= 0", rewriteOn = ArithmeticException.class)
    protected int intPow(int base, int exponent) {
        int ret = 1;
        for (int i = 0; i < exponent; i++) {
            ret = Math.multiplyExact(ret, base);
        }
        return ret;
    }

    @Specialization(replaces = "intPow")
    protected double doublePow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    /**
     * It's always possible to get called with 'undefined' passed as an argument,
     * or even another function.
     * Simply return NaN in all those cases.
     */
    @Fallback
    protected double nonNumberPow(@SuppressWarnings("unused") Object base, @SuppressWarnings("unused") Object exponent) {
        return Double.NaN;
    }
}
