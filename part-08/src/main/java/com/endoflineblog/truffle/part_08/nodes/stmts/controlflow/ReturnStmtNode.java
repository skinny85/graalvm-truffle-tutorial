package com.endoflineblog.truffle.part_08.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_08.exceptions.ReturnException;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/** A Node representing a 'return' statement. */
public final class ReturnStmtNode extends EasyScriptStmtNode {
    @Child
    private EasyScriptExprNode returnExpr;

    public ReturnStmtNode(EasyScriptExprNode returnExpr) {
        this.returnExpr = returnExpr;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object returnValue = this.returnExpr.executeGeneric(frame);
        throw new ReturnException(returnValue);
    }
}
