package com.endoflineblog.truffle.part_05.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents a decimal number literal expression in EasyScript.
 * The same as the DoubleLiteralNode in part 3.
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
