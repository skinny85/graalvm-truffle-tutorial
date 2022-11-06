package com.endoflineblog.truffle.part_10.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_10.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number subtraction.
 * Very similar to the {@link AdditionExprNode addition Node}.
 */
public abstract class SubtractionExprNode extends BinaryOperationExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int subtractInts(int leftValue, int rightValue) {
        return Math.subtractExact(leftValue, rightValue);
    }

    @Specialization(replaces = "subtractInts")
    protected double subtractDoubles(double leftValue, double rightValue) {
        return leftValue - rightValue;
    }

    /** Non-numbers cannot be subtracted, and always result in NaN. */
    @Fallback
    protected double subtractNonNumber(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}
