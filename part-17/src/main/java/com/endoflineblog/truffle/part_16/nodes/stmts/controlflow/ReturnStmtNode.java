package com.endoflineblog.truffle.part_16.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_16.exceptions.ReturnException;
import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node representing the {@code return} statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we get rid of the {@link #getSourceSection()}
 * method, since it has now been moved into {@link EasyScriptStmtNode},
 * to which we pass the {@link SourceSection} through its constructor.
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
