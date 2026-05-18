package com.endoflineblog.truffle.part_17.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_17.exceptions.ContinueException;
import com.endoflineblog.truffle.part_17.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node representing the {@code continue} statement.
 * Identical to the class with the same name from part 16.
 */
public final class ContinueStmtNode extends EasyScriptStmtNode {
    public ContinueStmtNode(SourceSection sourceSection) {
        super(sourceSection);
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new ContinueException();
    }
}
