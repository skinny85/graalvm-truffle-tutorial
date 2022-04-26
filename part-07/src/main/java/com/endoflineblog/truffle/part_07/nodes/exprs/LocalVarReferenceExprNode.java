package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.nodes.stmts.LocalVarDeclStmtNode;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LocalVarReferenceExprNode extends EasyScriptExprNode {
    private final FrameSlot frameSlot;

    public LocalVarReferenceExprNode(FrameSlot frameSlot) {
        this.frameSlot = frameSlot;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object ret = frame.getValue(this.frameSlot);
        if (ret == LocalVarDeclStmtNode.DUMMY) {
            throw new EasyScriptException("Cannot access '" + this.frameSlot.getIdentifier() + "' before initialization");
        }
        return ret; // ToDo add specializations handling
    }
}
