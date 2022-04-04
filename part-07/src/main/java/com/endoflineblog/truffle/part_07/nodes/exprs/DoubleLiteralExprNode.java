package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents an decimal number literal expression in EasyScript.
 * Identical to the class with the same name from part 5.
 */
public final class DoubleLiteralExprNode extends EasyScriptExprNode {
    private final double value;

    public DoubleLiteralExprNode(double value) {
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

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(this.value);
    }
}
