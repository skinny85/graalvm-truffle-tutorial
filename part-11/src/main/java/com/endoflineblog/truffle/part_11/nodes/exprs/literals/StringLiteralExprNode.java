package com.endoflineblog.truffle.part_11.nodes.exprs.literals;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The AST node that represents a string literal expression in EasyScript.
 */
@NodeField(name = "value", type = String.class)
public abstract class StringLiteralExprNode extends EasyScriptExprNode {
    protected abstract String getValue();

    @Override
    public boolean executeBool(VirtualFrame frame) {
        return !this.getValue().isEmpty();
    }

    @Specialization
    protected TruffleString createTruffleString(
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode) {
        return fromJavaStringNode.execute(this.getValue(), TruffleString.Encoding.UTF_16);
    }
}
