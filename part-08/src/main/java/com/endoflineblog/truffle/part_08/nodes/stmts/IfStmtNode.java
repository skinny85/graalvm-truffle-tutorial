package com.endoflineblog.truffle.part_08.nodes.stmts;

import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

public final class IfStmtNode extends EasyScriptStmtNode {
    @Child
    private EasyScriptExprNode conditionExpr;

    @Child
    private EasyScriptStmtNode thenStmt;

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
            this.thenStmt.executeStatement(frame);
        } else {
            if (this.elseStmt != null) {
                this.elseStmt.executeStatement(frame);
            }
        }
        return Undefined.INSTANCE;
    }
}
