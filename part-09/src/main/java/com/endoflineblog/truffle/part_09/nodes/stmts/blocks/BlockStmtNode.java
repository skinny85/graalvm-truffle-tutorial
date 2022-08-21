package com.endoflineblog.truffle.part_09.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * A Node for representing the EasyScript program itself,
 * and any statement blocks,
 * for example those used as the branch of an {@code if} statement
 * (with the exception of the block for a user-defined function's body,
 * which is represented by {@link UserFuncBodyStmtNode}).
 * Identical to the class with the same name from part 7.
 */
public final class BlockStmtNode extends com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode {
    @Children
    private final com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode[] stmts;

    public BlockStmtNode(List<com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode> stmts) {
        this.stmts = stmts.toArray(new com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode[]{});
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns the result of executing the last statement.
     */
    @Override
    @ExplodeLoop
    public Object executeStatement(VirtualFrame frame) {
        Object ret = Undefined.INSTANCE;
        for (EasyScriptStmtNode stmt : this.stmts) {
            ret = stmt.executeStatement(frame);
        }
        return ret;
    }
}
