package com.endoflineblog.truffle.part_16.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_16.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * Node class representing the lesser ({@code <}) operator.
 * Identical to the class with the same name from part 15.
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

    @Specialization
    protected boolean stringLesser(TruffleString leftValue, TruffleString rightValue,
            @Cached TruffleString.CompareCharsUTF16Node compareNode) {
        return compareNode.execute(leftValue, rightValue) < 0;
    }

    @Fallback
    protected boolean objectLesser(Object leftValue, Object rightValue) {
        return false;
    }
}
