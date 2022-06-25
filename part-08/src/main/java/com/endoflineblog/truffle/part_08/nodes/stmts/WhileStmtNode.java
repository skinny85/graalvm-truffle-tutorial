package com.endoflineblog.truffle.part_08.nodes.stmts;

import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;

public final class WhileStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private LoopNode loopNode;

    public WhileStmtNode(EasyScriptExprNode conditionExpr, EasyScriptStmtNode bodyStmt) {
        this.loopNode = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(conditionExpr, bodyStmt));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.loopNode.execute(frame);
        return Undefined.INSTANCE;
    }

    private static final class WhileRepeatingNode extends Node implements RepeatingNode {
        @SuppressWarnings("FieldMayBeFinal")
        @Child
        private EasyScriptExprNode conditionExpr;

        @SuppressWarnings("FieldMayBeFinal")
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
}
