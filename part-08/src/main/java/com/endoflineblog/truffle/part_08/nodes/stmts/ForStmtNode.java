package com.endoflineblog.truffle.part_08.nodes.stmts;

import com.endoflineblog.truffle.part_08.BreakException;
import com.endoflineblog.truffle.part_08.ContinueException;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;

public final class ForStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode initStmt;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private LoopNode loopNode;

    public ForStmtNode(EasyScriptStmtNode initStmt, EasyScriptExprNode conditionExpr,
            EasyScriptExprNode updateExpr, EasyScriptStmtNode bodyStmt) {
        this.initStmt = initStmt;
        this.loopNode = Truffle.getRuntime().createLoopNode(
                new ForRepeatingNode(conditionExpr, updateExpr, bodyStmt));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        // first, we execute the init statement, if provided
        if (this.initStmt != null) {
            this.initStmt.executeStatement(frame);
        }
        this.loopNode.execute(frame);
        return Undefined.INSTANCE;
    }

    private static final class ForRepeatingNode extends Node implements RepeatingNode {
        @SuppressWarnings("FieldMayBeFinal")
        @Child
        private EasyScriptExprNode conditionExpr;

        @SuppressWarnings("FieldMayBeFinal")
        @Child
        private EasyScriptExprNode updateExpr;

        @SuppressWarnings("FieldMayBeFinal")
        @Child
        private EasyScriptStmtNode bodyStmt;

        public ForRepeatingNode(EasyScriptExprNode conditionExpr, EasyScriptExprNode updateExpr,
                EasyScriptStmtNode bodyStmt) {
            this.conditionExpr = conditionExpr;
            this.updateExpr = updateExpr;
            this.bodyStmt = bodyStmt;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            if (this.conditionExpr != null &&
                    !this.conditionExpr.executeBool(frame)) {
                return false;
            }
            try {
                this.bodyStmt.executeStatement(frame);
            } catch (BreakException e) {
                // 'break' means 'stop the loop'
                return false;
            } catch (ContinueException e) {
                // fall-through on 'continue'
            }
            if (this.updateExpr != null) {
                this.updateExpr.executeGeneric(frame);
            }
            return true;
        }
    }
}
