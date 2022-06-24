package com.endoflineblog.truffle.part_08.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_08.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class GreaterOrEqualExprNode extends BinaryOperationExprNode {
    @Specialization
    protected boolean intGreaterOrEqual(int leftValue, int rightValue) {
        return leftValue >= rightValue;
    }

    @Specialization(replaces = "intGreaterOrEqual")
    protected boolean doubleGreaterOrEqual(double leftValue, double rightValue) {
        return leftValue >= rightValue;
    }

    @Fallback
    protected boolean objectGreaterOrEqual(Object leftValue, Object rightValue) {
        return false;
    }
}
