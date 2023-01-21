package com.endoflineblog.truffle.part_11.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_11.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * Node class representing the lesser ({@code <}) operator.
 * Identical to the class with the same name from part 9.
 */
public abstract class LesserExprNode extends BinaryOperationExprNode {
    @Specialization
    protected boolean intLesser(int leftValue, int rightValue) {
        return leftValue < rightValue;
    }

    @Specialization(replaces = "intLesser")
    protected boolean doubleLesser(double leftValue, double rightValue) {
        return leftValue < rightValue;
    }

    @Fallback
    protected boolean objectLesser(Object leftValue, Object rightValue) {
        return false;
    }
}
