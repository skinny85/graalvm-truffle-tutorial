package com.endoflineblog.truffle.part_15.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_15.exceptions.ContinueException;
import com.endoflineblog.truffle.part_15.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node representing the {@code continue} statement.
 * Identical to the class with the same name from part 14.
 */
public final class ContinueStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ContinueException();
    }
}
