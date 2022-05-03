package com.endoflineblog.truffle.part_07.nodes.exprs.functions;

import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class WriteFunctionArgExprNode extends EasyScriptExprNode {
    private final int index;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;

    public WriteFunctionArgExprNode(int index, EasyScriptExprNode initializerExpr) {
        this.index = index;
        this.initializerExpr = initializerExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object value = this.initializerExpr.executeGeneric(frame);
        // we are guaranteed the argument array has enough elements
        frame.getArguments()[this.index] = value;
        return value;
    }
}
