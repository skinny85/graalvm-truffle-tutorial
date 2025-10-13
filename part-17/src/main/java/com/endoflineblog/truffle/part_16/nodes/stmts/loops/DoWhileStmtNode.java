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
 * A Node that represents a {@code do-while} statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we add a {@link SourceSection}
 * parameter to the constructor, which we pass to the constructor of the superclass,
 * {@link EasyScriptStmtNode}.
 */
public final class DoWhileStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private LoopNode loopNode;

    public DoWhileStmtNode(
            EasyScriptExprNode conditionExpr, EasyScriptStmtNode bodyStmt, SourceSection sourceSection) {
        super(sourceSection);
        this.loopNode = Truffle.getRuntime().createLoopNode(new DoWhileRepeatingNode(conditionExpr, bodyStmt));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.loopNode.execute(frame);
        return Undefined.INSTANCE;
    }

    private static final class DoWhileRepeatingNode extends Node implements RepeatingNode {
        @SuppressWarnings("FieldMayBeFinal")
        @Child
        private EasyScriptExprNode conditionExpr;

        @SuppressWarnings("FieldMayBeFinal")
        @Child
        private EasyScriptStmtNode bodyStmt;

        public DoWhileRepeatingNode(EasyScriptExprNode conditionExpr, EasyScriptStmtNode bodyStmt) {
            this.conditionExpr = conditionExpr;
            this.bodyStmt = bodyStmt;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            // in a do-while, we first execute the body of the loop
            try {
                this.bodyStmt.executeStatement(frame);
            } catch (BreakException e) {
                // 'break' means 'stop the loop'
                return false;
            } catch (ContinueException e) {
                // fall-through on 'continue'
            }
            return this.conditionExpr.executeBool(frame);
        }
    }
}
