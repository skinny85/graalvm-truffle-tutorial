package com.endoflineblog.truffle.part_09.nodes.exprs.literals;

import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents a boolean literal expression in EasyScript.
 * Identical to the class with the same name from part 8.
 */
public final class BoolLiteralExprNode extends EasyScriptExprNode {
    private final boolean value;

    public BoolLiteralExprNode(boolean value) {
        this.value = value;
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(this.value);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(this.value);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
