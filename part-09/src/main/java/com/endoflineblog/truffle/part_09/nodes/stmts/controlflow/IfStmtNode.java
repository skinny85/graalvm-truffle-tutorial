package com.endoflineblog.truffle.part_09.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

/**
 * A Node that represents an {@code if} statement.
 */
public final class IfStmtNode extends com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode {
    @Child
    private EasyScriptExprNode conditionExpr;

    @Child
    private com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode thenStmt;

    @Child
    private com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode elseStmt;

    private final ConditionProfile condition = ConditionProfile.createCountingProfile();

    public IfStmtNode(EasyScriptExprNode conditionExpr, com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode thenStmt, EasyScriptStmtNode elseStmt) {
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
