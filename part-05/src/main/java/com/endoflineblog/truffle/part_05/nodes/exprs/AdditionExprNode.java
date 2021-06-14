package com.endoflineblog.truffle.part_05.nodes.exprs;

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
}
