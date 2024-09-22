package com.endoflineblog.truffle.part_15.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_15.exceptions.ReturnException;
import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_15.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node representing the {@code return} statement.
 * Almost identical to the class with the same name from part 14,
 * the only difference is that we override the {@link #getSourceSection()}
 * method from the {@link com.oracle.truffle.api.nodes.Node} class,
 * which is used for filling exception stack traces.
 */
public final class ReturnStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode returnExpr;

    private final SourceSection sourceSection;

    public ReturnStmtNode(EasyScriptExprNode returnExpr, SourceSection sourceSection) {
        this.returnExpr = returnExpr;
        this.sourceSection = sourceSection;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object returnValue = this.returnExpr.executeGeneric(frame);
        throw new ReturnException(returnValue);
    }

    @Override
    public SourceSection getSourceSection() {
        return this.sourceSection;
    }
}
