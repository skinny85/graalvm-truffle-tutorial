package com.endoflineblog.truffle.part_10.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_10.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * Node class representing the lesser or equal ({@code <=}) operator.
 * Identical to the class with the same name from part 9.
 */
public abstract class LesserOrEqualExprNode extends BinaryOperationExprNode {
    @Specialization
    protected boolean intLesserOrEqual(int leftValue, int rightValue) {
        return leftValue <= rightValue;
    }

    @Specialization(replaces = "intLesserOrEqual")
    protected boolean doubleLesserOrEqual(double leftValue, double rightValue) {
        return leftValue <= rightValue;
    }

    @Fallback
    protected boolean objectLesserOrEqual(Object leftValue, Object rightValue) {
        return false;
    }
}
