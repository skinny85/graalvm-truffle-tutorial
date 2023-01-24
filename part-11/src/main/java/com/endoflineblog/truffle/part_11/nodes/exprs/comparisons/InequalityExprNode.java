package com.endoflineblog.truffle.part_11.nodes.exprs.comparisons;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.BinaryOperationExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

/**
 * Node class representing the strict inequality ({@code !==}) operator.
 * Identical to the class with the same name from part 9.
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

    @Specialization(guards = {
            "leftValueInteropLibrary.isString(leftValue)",
            "rightValueInteropLibrary.isString(rightValue)"
    }, limit = "1")
    protected boolean stringInequality(Object leftValue, Object rightValue,
            @CachedLibrary("leftValue") InteropLibrary leftValueInteropLibrary,
            @CachedLibrary("rightValue") InteropLibrary rightValueInteropLibrary) {
        try {
            return !leftValueInteropLibrary.asString(leftValue).equals(
                    rightValueInteropLibrary.asString(rightValue));
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    @Fallback
    protected boolean objectEquality(Object leftValue, Object rightValue) {
        return leftValue != rightValue;
    }
}
