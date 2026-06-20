package com.endoflineblog.truffle.part_17.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_17.exceptions.ReturnException;
import com.endoflineblog.truffle.part_17.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_17.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node representing the {@code return} statement.
 * Identical to the class with the same name from part 16.
 */
public final class ReturnStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode returnExpr;

    public ReturnStmtNode(EasyScriptExprNode returnExpr, SourceSection sourceSection) {
        super(sourceSection);
        this.returnExpr = returnExpr;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object returnValue = this.returnExpr.executeGeneric(frame);
        throw new ReturnException(returnValue);
    }
}
