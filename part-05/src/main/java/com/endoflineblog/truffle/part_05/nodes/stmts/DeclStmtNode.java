package com.endoflineblog.truffle.part_05.nodes.stmts;

import com.endoflineblog.truffle.part_05.nodes.exprs.AssignmentExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a variable or constant in EasyScript.
 * Simply delegates to the assignment expression.
 */
public final class DeclStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private AssignmentExprNode assignmentExpression;

    public DeclStmtNode(AssignmentExprNode assignmentExpression) {
        this.assignmentExpression = assignmentExpression;
    }

    /**
     * The sensible thing here would probably to return `undefined`,
     * but since we don't have it in the language yet,
     * simply return the value of the variable instead.
     */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        return this.assignmentExpression.executeGeneric(frame);
    }
}
