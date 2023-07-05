package com.endoflineblog.truffle.part_12.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_12.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * Node class representing the strict inequality ({@code !==}) operator.
 * Almost identical to the class with the same name from part 10,
 * the only difference is that we add a specialization for comparing
 * {@link TruffleString}s for inequality.
 *
 * @see #stringInequality
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

    @Specialization
    protected boolean stringInequality(TruffleString leftValue, TruffleString rightValue,
            @Cached TruffleString.EqualNode equalNode) {
        return !EasyScriptTruffleStrings.equals(leftValue, rightValue, equalNode);
    }

    @Fallback
    protected boolean objectInequality(Object leftValue, Object rightValue) {
        return leftValue != rightValue;
    }
}
