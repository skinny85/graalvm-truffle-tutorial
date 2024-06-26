package com.endoflineblog.truffle.part_14.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_14.exceptions.ReturnException;
import com.endoflineblog.truffle.part_14.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_14.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node representing the {@code return} statement.
 * Identical to the class with the same name from part 13.
 */
public final class ReturnStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
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
