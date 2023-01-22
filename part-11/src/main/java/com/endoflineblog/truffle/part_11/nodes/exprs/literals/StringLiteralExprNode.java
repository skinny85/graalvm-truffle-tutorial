package com.endoflineblog.truffle.part_11.nodes.exprs.literals;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents a string literal expression in EasyScript.
 */
public final class StringLiteralExprNode extends EasyScriptExprNode {
    private final String value;

    public StringLiteralExprNode(String value) {
        this.value = value;
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return !this.value.isEmpty();
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
    public String executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
