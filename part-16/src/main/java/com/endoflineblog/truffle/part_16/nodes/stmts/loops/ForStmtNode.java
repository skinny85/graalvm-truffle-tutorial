package com.endoflineblog.truffle.part_16.nodes.stmts.loops;

import com.endoflineblog.truffle.part_16.exceptions.BreakException;
import com.endoflineblog.truffle.part_16.exceptions.ContinueException;
import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node that represents a {@code for} statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we add a {@link SourceSection}
 * parameter to the constructor, which we pass to the constructor of the superclass,
 * {@link EasyScriptStmtNode}.
 */
public final class ForStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode initStmt;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private LoopNode loopNode;

    public ForStmtNode(EasyScriptStmtNode initStmt, EasyScriptExprNode conditionExpr,
            EasyScriptExprNode updateExpr, EasyScriptStmtNode bodyStmt,
            SourceSection sourceSection) {
        super(sourceSection);
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
