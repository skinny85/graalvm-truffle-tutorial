package com.endoflineblog.truffle.part_08.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_08.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * Node class representing the strict inequality ({@code !==}) operator.
 */
public abstract class InequalityExprNode extends BinaryOperationExprNode {
    @Specialization
    protected boolean intEquality(int leftValue, int rightValue) {
        return leftValue != rightValue;
    }

    @Specialization(replaces = "intEquality")
    protected boolean doubleEquality(double leftValue, double rightValue) {
        return leftValue != rightValue;
    }

    @Specialization
    protected boolean boolEquality(boolean leftValue, boolean rightValue) {
        return leftValue != rightValue;
    }

    @Fallback
    protected boolean objectEquality(Object leftValue, Object rightValue) {
        return leftValue != rightValue;
    }
}
