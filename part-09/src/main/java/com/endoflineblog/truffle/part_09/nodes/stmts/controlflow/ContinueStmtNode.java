package com.endoflineblog.truffle.part_09.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_09.exceptions.ContinueException;
import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/** A Node representing the {@code continue} statement. */
public final class ContinueStmtNode extends EasyScriptStmtNode {
    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ContinueException();
    }
}
