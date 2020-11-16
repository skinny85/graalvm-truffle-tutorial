package com.endoflineblog.truffle.part_02;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents an decimal number literal expression in EasyScript.
 * Since we have to introduce `double` values anyway to correctly handle the JavaScript semantics of number addition,
 * we might as well add floating-point literals to the language at this point.
 */
public final class DoubleLiteralNode extends EasyScriptNode {
    private final double value;

    public DoubleLiteralNode(double value) {
        this.value = value;
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.executeDouble(frame);
    }

    /**
     * Since it's not possible to represent a `double` value as an `int`,
     * always throw {@link UnexpectedResultException} here.
     */
    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        // notice that we pass the value of the Node to the exception's constructor
        throw new UnexpectedResultException(this.value);
    }
}
