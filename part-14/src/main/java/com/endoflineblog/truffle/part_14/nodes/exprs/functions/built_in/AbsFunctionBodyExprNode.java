package com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * An expression Node that represents the implementation of the
 * {@code Math.abs()} JavaScript function.
 * Identical to the class with the same name from part 13.
 */
public abstract class AbsFunctionBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intAbs(int argument) {
        // Integer.MIN_VALUE is too big to fit negated into an int
        return argument < 0 ? Math.negateExact(argument) : argument;
    }

    @Specialization(replaces = "intAbs")
    protected double doubleAbs(double argument) {
        return Math.abs(argument);
    }

    /**
     * It's always possible to get called with 'undefined' passed as an argument,
     * or even another function.
     * Simply return NaN in all those cases.
     */
    @Fallback
    protected double nonNumberAbs(@SuppressWarnings("unused") Object argument) {
        return Double.NaN;
    }
}
