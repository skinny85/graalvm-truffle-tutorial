package com.endoflineblog.truffle.part_08.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_08.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * Node class representing the strict inequality ({@code !==}) operator.
 */
public abstract class InequalityExprNode extends BinaryOperationExprNode {
    @Specialization
    protected boolean intInequality(int leftValue, int rightValue) {
        return leftValue != rightValue;
    }

    @Specialization(replaces = "intInequality")
    protected boolean doubleInequality(double leftValue, double rightValue) {
        return leftValue != rightValue;
    }

    @Specialization
    protected boolean boolInequality(boolean leftValue, boolean rightValue) {
        return leftValue != rightValue;
    }

    @Fallback
    protected boolean objectInequality(Object leftValue, Object rightValue) {
        return leftValue != rightValue;
    }
}
