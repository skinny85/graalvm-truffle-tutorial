package com.endoflineblog.truffle.part_14.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_14.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number subtraction.
 * Identical to the class with the same name from part 13.
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
