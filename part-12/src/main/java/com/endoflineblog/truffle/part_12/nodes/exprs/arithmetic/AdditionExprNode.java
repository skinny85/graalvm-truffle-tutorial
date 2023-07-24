package com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_12.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.AdditionOrConcatenationOperationNode;
import com.endoflineblog.truffle.part_12.nodes.ops.AdditionOrConcatenationOperationNodeGen;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 */
public abstract class AdditionExprNode extends BinaryOperationExprNode {
    private final AdditionOrConcatenationOperationNode additionOrConcatenationOperationNode;

    protected AdditionExprNode() {
        this.additionOrConcatenationOperationNode = AdditionOrConcatenationOperationNodeGen.create();
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return this.additionOrConcatenationOperationNode.executeOperationInt(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return this.additionOrConcatenationOperationNode.executeOperationDouble(leftValue, rightValue);
    }

    @Fallback
    protected Object addNonNumber(Object leftValue, Object rightValue) {
        return this.additionOrConcatenationOperationNode.executeOperation(leftValue, rightValue);
    }
}
