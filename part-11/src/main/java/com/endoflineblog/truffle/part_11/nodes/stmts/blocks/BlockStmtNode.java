package com.endoflineblog.truffle.part_11.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_11.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;

import java.util.List;

/**
 * A Node for representing the EasyScript program itself,
 * and any statement blocks,
 * for example those used as the branch of an {@code if} statement
 * (with the exception of the block for a user-defined function's body,
 * which is represented by {@link UserFuncBodyStmtNode}).
 * Identical to the class with the same name from part 9.
 *
 * @see #executeStatement
 */
public final class BlockStmtNode extends EasyScriptStmtNode
        implements BlockNode.ElementExecutor<EasyScriptStmtNode> {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockNode<EasyScriptStmtNode> block;

    public BlockStmtNode(List<EasyScriptStmtNode> stmts) {
        this.block = stmts.size() > 0
                ? BlockNode.create(stmts.toArray(new EasyScriptStmtNode[]{}), this)
                : null;
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns the result of executing the last statement.
     */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        return this.block == null
                ? Undefined.INSTANCE
                : this.block.executeGeneric(frame, BlockNode.NO_ARGUMENT);
    }

    /**
     * This is a method from the
     * {@link BlockNode.ElementExecutor} interface
     * that executes a single statement from the block,
     * discarding its result.
     * It's an abstract method, so it has to be overridden.
     */
    @Override
    public void executeVoid(VirtualFrame frame, EasyScriptStmtNode stmtNode, int index, int argument) {
        stmtNode.executeStatement(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame, EasyScriptStmtNode stmtNode, int index, int argument) {
        return stmtNode.executeStatement(frame);
    }
}
