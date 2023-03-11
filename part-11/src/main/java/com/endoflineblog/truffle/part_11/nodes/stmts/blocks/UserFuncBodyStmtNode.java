package com.endoflineblog.truffle.part_11.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_11.exceptions.ReturnException;
import com.endoflineblog.truffle.part_11.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;

import java.util.List;

/**
 * A Node for representing the statement blocks of a user-defined function in EasyScript.
 * Returns its value by catching {@link ReturnException}.
 * Identical to the class with the same name from part 9.
 */
public final class UserFuncBodyStmtNode extends EasyScriptStmtNode
        implements BlockNode.ElementExecutor<EasyScriptStmtNode> {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockNode<EasyScriptStmtNode> block;

    public UserFuncBodyStmtNode(List<EasyScriptStmtNode> stmts) {
        this.block = stmts.size() > 0
                ? BlockNode.create(stmts.toArray(new EasyScriptStmtNode[]{}), this)
                : null;
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns whatever a 'return' statement inside it returns.
     */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.block != null) {
            try {
                this.block.executeVoid(frame, BlockNode.NO_ARGUMENT);
            } catch (ReturnException e) {
                return e.returnValue;
            }
        }
        // if there was no return statement,
        // or the block didn't have any statements,
        // then we return 'undefined'
        return Undefined.INSTANCE;
    }

    @Override
    public void executeVoid(VirtualFrame frame, EasyScriptStmtNode stmtNode, int index, int argument) {
        stmtNode.executeStatement(frame);
    }
}
