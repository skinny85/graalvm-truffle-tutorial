package com.endoflineblog.truffle.part_11.nodes.exprs.literals;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.runtime.StringObject;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The AST node that represents a string literal expression in EasyScript.
 */
public final class StringLiteralExprNode extends EasyScriptExprNode {
    private final String value;

    @CompilationFinal
    private StringObject cachedLiteral;

    public StringLiteralExprNode(String value) {
        this.value = value;
    }

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return !this.value.isEmpty();
    }

    @Override
    public StringObject executeGeneric(VirtualFrame frame) {
        if (this.cachedLiteral == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.cachedLiteral = new StringObject(this.value, this.currentLanguageContext().stringPrototype);
        }
        return this.cachedLiteral;
    }
}
