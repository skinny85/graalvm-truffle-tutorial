package com.endoflineblog.truffle.part_04.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents an decimal number literal expression in EasyScript.
 * Pretty much identical to {@link com.endoflineblog.truffle.part_02.DoubleLiteralNode}.
 */
public final class DoubleLiteralNode extends EasyScriptNode {
    private final double value;

    public DoubleLiteralNode(double value) {
        this.value = value;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(this.value);
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.executeDouble(frame);
    }
}
