package com.endoflineblog.truffle.part_02;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents the '+' operator in EasyScript.
 * Correctly handles adding ints that overflow, and doubles.
 */
public final class AdditionNode extends EasyScriptNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptNode leftNode, rightNode;

    private enum SpecializationState { UNINITIALIZED, INT, DOUBLE}

    /**
     * The {@link CompilerDirectives.CompilationFinal}
     * is crucial for performance when Graal JIT-compiles this code
     * (it treats this field as a constant during partial evaluation).
     */
    @CompilerDirectives.CompilationFinal
    private SpecializationState specializationState;

    public AdditionNode(EasyScriptNode leftNode, EasyScriptNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.specializationState = SpecializationState.UNINITIALIZED;
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        double leftValue = this.leftNode.executeDouble(frame);
        double rightValue = this.rightNode.executeDouble(frame);
        return leftValue + rightValue;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        int leftValue;
        try {
            leftValue = this.leftNode.executeInt(frame);
        } catch (UnexpectedResultException e) {
            this.activateDoubleSpecialization();
            double leftDouble = (double) e.getResult();
            throw new UnexpectedResultException(leftDouble + this.rightNode.executeDouble(frame));
        }

        int rightValue;
        try {
            rightValue = this.rightNode.executeInt(frame);
        } catch (UnexpectedResultException e) {
            this.activateDoubleSpecialization();
            double rightDouble = (double) e.getResult();
            throw new UnexpectedResultException(leftValue + rightDouble);
        }

        try {
            return Math.addExact(leftValue, rightValue);
        } catch (ArithmeticException e) {
            this.activateDoubleSpecialization();
            throw new UnexpectedResultException((double) leftValue + (double) rightValue);
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (this.specializationState == SpecializationState.INT) {
            try {
                return this.executeInt(frame);
            } catch (UnexpectedResultException e) {
                this.activateDoubleSpecialization();
                return e.getResult();
            }
        }
        if (this.specializationState == SpecializationState.DOUBLE) {
            return this.executeDouble(frame);
        }
        // uninitialized case
        Object leftValue = this.leftNode.executeGeneric(frame);
        Object rightValue = this.rightNode.executeGeneric(frame);
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return this.executeAndSpecialize(leftValue, rightValue);
    }

    private Object executeAndSpecialize(Object leftValue, Object rightValue) {
        if (leftValue instanceof Integer && rightValue instanceof Integer) {
            try {
                int result = Math.addExact((int) leftValue, (int) rightValue);
                this.activateIntSpecialization();
                return result;
            } catch (ArithmeticException e) {
                // fall through to the double case below
            }
        }
        this.activateDoubleSpecialization();
        // one or both of the values might be Integers,
        // because of the && above, and the possibility of overflow
        return convertToDouble(leftValue) + convertToDouble(rightValue);
    }

    private void activateIntSpecialization() {
        this.specializationState = SpecializationState.INT;
    }

    private void activateDoubleSpecialization() {
        this.specializationState = SpecializationState.DOUBLE;
    }

    private static double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return (double) value;
    }
}
