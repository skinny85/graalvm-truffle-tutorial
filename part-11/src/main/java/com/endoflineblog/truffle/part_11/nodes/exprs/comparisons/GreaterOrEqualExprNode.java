package com.endoflineblog.truffle.part_11.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_11.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * Node class representing the greater or equal ({@code >=}) operator.
 * Almost identical to the class with the same name from part 10,
 * the only difference is that we add a specialization for comparing
 * {@link TruffleString}s for greater or equal.
 *
 * @see #stringGreaterOrEqual
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
