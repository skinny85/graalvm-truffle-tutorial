package com.endoflineblog.truffle.part_10.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_10.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_10.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * A Node for representing the EasyScript program itself,
 * and any statement blocks,
 * for example those used as the branch of an {@code if} statement
 * (with the exception of the block for a user-defined function's body,
 * which is represented by {@link UserFuncBodyStmtNode}).
 * Almost identical to the class with the same name from part 8,
 * the only difference is that we refactor the implementation of the
 * {@link #executeStatement} method to avoid performing redundant assignments,
 * which results in a 3x speedup in the Fibonacci benchmark.
 *
 * @see #executeStatement
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
    @ExplodeLoop
    public Object executeStatement(VirtualFrame frame) {
        // this implementation results in a 3x improvement in the Fibonacci benchmark,
        // compared to the implementation from part 8
        int stmtsMinusOne = this.stmts.length - 1;
        for (int i = 0; i < stmtsMinusOne; i++) {
            this.stmts[i].executeStatement(frame);
        }
        return stmtsMinusOne < 0 ? Undefined.INSTANCE : this.stmts[stmtsMinusOne].executeStatement(frame);
    }
}
