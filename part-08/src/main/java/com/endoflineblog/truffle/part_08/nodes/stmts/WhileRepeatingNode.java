package com.endoflineblog.truffle.part_08.nodes.stmts;

import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;

public final class WhileRepeatingNode extends Node implements RepeatingNode {
    @Child
    private EasyScriptExprNode conditionExpr;

    @Child
    private EasyScriptStmtNode bodyStmt;

    public WhileRepeatingNode(EasyScriptExprNode conditionExpr, EasyScriptStmtNode bodyStmt) {
        this.conditionExpr = conditionExpr;
        this.bodyStmt = bodyStmt;
    }

    @Override
    public boolean executeRepeating(VirtualFrame frame) {
        if (!this.conditionExpr.executeBool(frame)) {
            return false;
        }
        this.bodyStmt.executeStatement(frame);
        return true;
    }
}
