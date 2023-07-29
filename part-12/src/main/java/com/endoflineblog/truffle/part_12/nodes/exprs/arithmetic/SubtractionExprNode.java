package com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_12.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.ExternallySpecializingSubtractionOpNode;
import com.endoflineblog.truffle.part_12.nodes.ops.ExternallySpecializingSubtractionOpNodeGen;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number subtraction.
 */
public abstract class SubtractionExprNode extends BinaryOperationExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ExternallySpecializingSubtractionOpNode subtractionOpNode;

    protected SubtractionExprNode() {
        this.subtractionOpNode = ExternallySpecializingSubtractionOpNodeGen.create();
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected int subtractInts(int leftValue, int rightValue) {
        return this.subtractionOpNode.executeOpInt(leftValue, rightValue);
    }

    @Specialization(replaces = "subtractInts")
    protected double subtractDoubles(double leftValue, double rightValue) {
        return this.subtractionOpNode.executeOpDouble(leftValue, rightValue);
    }

    /**
     * Non-numbers cannot be subtracted, and always result in NaN.
     */
    @Fallback
    protected Object subtractNonNumber(Object leftValue, Object rightValue) {
        return this.subtractionOpNode.executeOp(leftValue, rightValue);
    }
}
