package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.List;

/**
 * A Node that represents a block of statements.
 * Used for representing the contents of the entire program,
 * and also the body of a user-defined function.
 */
public final class BlockStmtNode extends EasyScriptStmtNode {
    @Children
    private final EasyScriptStmtNode[] stmts;

    public BlockStmtNode(List<EasyScriptStmtNode> stmts) {
        this.stmts = stmts.toArray(new EasyScriptStmtNode[]{});
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns the result of executing the last statement.
     */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object ret = Undefined.INSTANCE;
        for (EasyScriptStmtNode stmt : this.stmts) {
            ret = stmt.executeStatement(frame);
        }
        return ret;
    }
}
