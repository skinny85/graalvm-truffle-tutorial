package com.endoflineblog.truffle.part_08.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_08.exceptions.BreakException;
import com.endoflineblog.truffle.part_08.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/** A Node representing the {@code break} statement. */
public final class BreakStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new BreakException();
    }
}
