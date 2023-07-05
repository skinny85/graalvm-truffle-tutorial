package com.endoflineblog.truffle.part_05.nodes.stmts;

import com.endoflineblog.truffle.part_05.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/** A Node that represents an expression statement. */
public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode expr;

    /**
     * Creates a new instance of the expression statement.
     *
     * @param expr the expression node
     */
    public ExprStmtNode(EasyScriptExprNode expr) {
        this.expr = expr;
    }

    /** Evaluating the statement returns the result of executing its expression. */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        return this.expr.executeGeneric(frame);
    }
}
