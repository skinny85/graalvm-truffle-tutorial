package com.endoflineblog.truffle.part_08.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_08.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 * Identical to the class with the same name from part 7.
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
     * Strictly speaking, booleans can be interpreted as numbers in JavaScript -
     * for example, {@code true + 3} evaluates to {@code 4}.
     * However, we won't bother implementing these sort of edge cases,
     * and we'll just return NaN for all of them.
     */
    @Fallback
    protected double addNonNumber(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}
