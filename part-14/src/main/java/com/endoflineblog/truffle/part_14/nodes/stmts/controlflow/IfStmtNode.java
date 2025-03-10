package com.endoflineblog.truffle.part_14.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_14.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_14.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_14.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

/**
 * A Node that represents an {@code if} statement.
 * Identical to the class with the same name from part 13.
 */
public final class IfStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode conditionExpr;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode thenStmt;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode elseStmt;

    private final ConditionProfile condition = ConditionProfile.createCountingProfile();

    public IfStmtNode(EasyScriptExprNode conditionExpr, EasyScriptStmtNode thenStmt, EasyScriptStmtNode elseStmt) {
        this.conditionExpr = conditionExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.condition.profile(this.conditionExpr.executeBool(frame))) {
            return this.thenStmt.executeStatement(frame);
        } else {
            return this.elseStmt == null
                    ? Undefined.INSTANCE
                    : this.elseStmt.executeStatement(frame);
        }
    }
}
