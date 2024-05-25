package com.endoflineblog.truffle.part_14.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_14.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * Node class representing the lesser or equal ({@code <=}) operator.
 * Identical to the class with the same name from part 13.
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

    @Specialization
    protected boolean stringLesserOrEqual(TruffleString leftValue, TruffleString rightValue,
            @Cached TruffleString.CompareCharsUTF16Node compareNode) {
        return compareNode.execute(leftValue, rightValue) <= 0;
    }

    @Fallback
    protected boolean objectLesserOrEqual(Object leftValue, Object rightValue) {
        return false;
    }
}
