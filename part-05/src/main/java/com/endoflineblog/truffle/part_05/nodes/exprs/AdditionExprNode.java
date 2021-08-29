package com.endoflineblog.truffle.part_05.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 * Same as AdditionNode in part 3.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class AdditionExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return Math.addExact(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return leftValue + rightValue;
    }

    /**
     * This specialization is a "slow" fallback in case any of the children
     * of this addition node are ever 'undefined'
     * (which is the only possible value in this chapter's version of EasyScript that is not an int or double).
     * In that case, in accordance with JavaScript semantics, we return Double.NaN.
     */
    @Fallback
    protected double addWithUndefined(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}
