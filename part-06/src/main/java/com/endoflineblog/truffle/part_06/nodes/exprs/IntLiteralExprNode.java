package com.endoflineblog.truffle.part_06.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The AST node that represents an integer literal expression in EasyScript.
 * Same as IntLiteralNode from part 3.
 */
public final class IntLiteralExprNode extends EasyScriptExprNode {
    private final int value;

    public IntLiteralExprNode(int value) {
        this.value = value;
    }

    @Override
    public int executeInt(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
