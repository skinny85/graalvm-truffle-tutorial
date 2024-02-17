package com.endoflineblog.truffle.part_12.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_12.exceptions.ContinueException;
import com.endoflineblog.truffle.part_12.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node representing the {@code continue} statement.
 * Identical to the class with the same name from part 11.
 */
public final class ContinueStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ContinueException();
    }
}
