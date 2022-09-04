package com.endoflineblog.truffle.part_09.nodes.exprs.literals;

import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents a decimal number literal expression in EasyScript.
 * Almost identical to the class with the same name from part 7,
 * the only difference is the additional {@link #executeBool}
 * method from {@link EasyScriptExprNode}.
 *
 * @see #executeBool
 */
public final class DoubleLiteralExprNode extends EasyScriptExprNode {
    private final double value;

    public DoubleLiteralExprNode(double value) {
        this.value = value;
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return this.value != 0.0;
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
