package com.endoflineblog.truffle.part_09.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_09.exceptions.ReturnException;
import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * A Node for representing the statement blocks of a user-defined function in EasyScript.
 * Returns its value by catching {@link ReturnException}.
 */
public final class UserFuncBodyStmtNode extends EasyScriptStmtNode {
    @Children
    private final EasyScriptStmtNode[] stmts;

    public UserFuncBodyStmtNode(List<EasyScriptStmtNode> stmts) {
        this.stmts = stmts.toArray(new EasyScriptStmtNode[]{});
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns whatever a 'return' statement inside it returns.
     */
    @Override
    @ExplodeLoop
    public Object executeStatement(VirtualFrame frame) {
        for (EasyScriptStmtNode stmt : this.stmts) {
            try {
                stmt.executeStatement(frame);
            } catch (ReturnException e) {
                return e.returnValue;
            }
        }
        // if there was no return statement,
        // then we return 'undefined'
        return Undefined.INSTANCE;
    }
}
