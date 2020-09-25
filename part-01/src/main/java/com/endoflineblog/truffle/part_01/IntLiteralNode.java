package com.endoflineblog.truffle.part_01;

import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The AST node that represents an integer literal expression in EasyScript.
 */
public final class IntLiteralNode extends EasyScriptNode {
    private final int value;

    public IntLiteralNode(int value) {
        this.value = value;
    }

    @Override
    public int executeInt(VirtualFrame frame) {
        return this.value;
    }
}
