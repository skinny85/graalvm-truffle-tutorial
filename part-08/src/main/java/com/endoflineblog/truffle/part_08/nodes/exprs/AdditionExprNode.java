package com.endoflineblog.truffle.part_08.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 * Identical to the class with the same name from part 6.
 */
public abstract class AdditionExprNode extends BinaryOperationExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return Math.addExact(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return leftValue + rightValue;
    }

    /**
     * Strictly speaking, adding functions results in turning them to strings in JavaScript.
     * However, since we don't have strings in EasyScript yet,
     * let's just keep returning NaN for additions involving any non-numbers.
     */
    @Fallback
    protected double addNonNumber(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}
