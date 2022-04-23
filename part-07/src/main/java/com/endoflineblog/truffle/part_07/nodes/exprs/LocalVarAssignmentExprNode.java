package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LocalVarAssignmentExprNode extends EasyScriptExprNode {
    private final FrameSlot frameSlot;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;

    public LocalVarAssignmentExprNode(FrameSlot frameSlot, EasyScriptExprNode initializerExpr) {
        this.frameSlot = frameSlot;
        this.initializerExpr = initializerExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object varValue = this.initializerExpr.executeGeneric(frame);
        frame.setObject(this.frameSlot, varValue); // ToDo add specializations handling
        return varValue;
    }
}
