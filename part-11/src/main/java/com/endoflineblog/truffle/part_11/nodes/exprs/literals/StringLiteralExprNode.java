package com.endoflineblog.truffle.part_11.nodes.exprs.literals;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The AST node that represents a string literal expression in EasyScript.
 */
public final class StringLiteralExprNode extends EasyScriptExprNode {
    private final TruffleString value;

    public StringLiteralExprNode(String value) {
        this.value = EasyScriptTruffleStrings.fromJavaString(value);
    }

    /** Empty strings are considered "falsy" in JavaScript. */
    @Override
    public boolean executeBool(VirtualFrame frame) {
        return !this.value.isEmpty();
    }

    @Override
    public TruffleString executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
