package com.endoflineblog.truffle.part_12.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_12.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * Node class representing the greater or equal ({@code >=}) operator.
 * Identical to the class with the same name from part 11.
 */
public abstract class GreaterOrEqualExprNode extends BinaryOperationExprNode {
    @Specialization
    protected boolean intGreaterOrEqual(int leftValue, int rightValue) {
        return leftValue >= rightValue;
    }

    @Specialization(replaces = "intGreaterOrEqual")
    protected boolean doubleGreaterOrEqual(double leftValue, double rightValue) {
        return leftValue >= rightValue;
    }

    @Specialization
    protected boolean stringGreaterOrEqual(TruffleString leftValue, TruffleString rightValue,
            @Cached TruffleString.CompareCharsUTF16Node compareNode) {
        return compareNode.execute(leftValue, rightValue) >= 0;
    }

    @Fallback
    protected boolean objectGreaterOrEqual(Object leftValue, Object rightValue) {
        return false;
    }
}
