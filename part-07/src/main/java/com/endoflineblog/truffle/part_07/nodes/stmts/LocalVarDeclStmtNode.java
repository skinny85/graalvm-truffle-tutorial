package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.LocalVarAssignmentExprNode;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LocalVarDeclStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private LocalVarAssignmentExprNode localVarAssignmentExprNode;

    public LocalVarDeclStmtNode(FrameSlot frameSlot, EasyScriptExprNode initializerExpr) {
        this.localVarAssignmentExprNode = new LocalVarAssignmentExprNode(frameSlot, initializerExpr);
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.localVarAssignmentExprNode.executeGeneric(frame);
        // a definition of a local variable returns undefined,
        // same as a definition of a global variable
        return Undefined.INSTANCE;
    }
}
