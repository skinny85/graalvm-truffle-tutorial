package com.endoflineblog.truffle.part_11.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_11.exceptions.BreakException;
import com.endoflineblog.truffle.part_11.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node representing the {@code break} statement.
 * Identical to the class with the same name from part 10.
 */
public final class BreakStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new BreakException();
    }
}
