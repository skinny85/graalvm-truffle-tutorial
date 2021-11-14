package com.endoflineblog.truffle.part_06.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 * Identical to the class with the same name from part 5.
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
     * Strictly speaking, adding functions results in turning them to strings in JavaScript.
     * However, since we don't have strings in EasyScript yet,
     * let's just keep returning NaN for additions involving any non-numbers.
     */
    @Fallback
    protected double addNonNumber(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}
