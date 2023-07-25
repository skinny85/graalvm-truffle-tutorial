package com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_12.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.AdditionConcatenationOperationNode;
import com.endoflineblog.truffle.part_12.nodes.ops.AdditionConcatenationOperationNodeGen;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 */
public abstract class AdditionExprNode extends BinaryOperationExprNode {
    private final AdditionConcatenationOperationNode additionConcatenationOperationNode;

    protected AdditionExprNode() {
        this.additionConcatenationOperationNode = AdditionConcatenationOperationNodeGen.create();
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return this.additionConcatenationOperationNode.executeOperationInt(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return this.additionConcatenationOperationNode.executeOperationDouble(leftValue, rightValue);
    }

    @Fallback
    protected Object addNonNumber(Object leftValue, Object rightValue) {
        return this.additionConcatenationOperationNode.executeOperation(leftValue, rightValue);
    }
}
