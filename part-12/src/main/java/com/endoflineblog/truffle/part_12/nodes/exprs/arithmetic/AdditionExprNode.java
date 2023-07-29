package com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_12.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.ExternallySpecializingAdditionConcatenationOpNode;
import com.endoflineblog.truffle.part_12.nodes.ops.ExternallySpecializingAdditionConcatenationOpNodeGen;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 */
public abstract class AdditionExprNode extends BinaryOperationExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ExternallySpecializingAdditionConcatenationOpNode additionConcatenationOperationNode;

    protected AdditionExprNode() {
        this.additionConcatenationOperationNode = ExternallySpecializingAdditionConcatenationOpNodeGen.create();
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return this.additionConcatenationOperationNode.executeOpInt(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return this.additionConcatenationOperationNode.executeOpDouble(leftValue, rightValue);
    }

    @Fallback
    protected Object addNonNumber(Object leftValue, Object rightValue) {
        return this.additionConcatenationOperationNode.executeOp(leftValue, rightValue);
    }
}
