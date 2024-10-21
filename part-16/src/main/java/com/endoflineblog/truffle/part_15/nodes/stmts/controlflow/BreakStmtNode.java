package com.endoflineblog.truffle.part_15.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_15.exceptions.BreakException;
import com.endoflineblog.truffle.part_15.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node representing the {@code break} statement.
 * Identical to the class with the same name from part 14.
 */
public final class BreakStmtNode extends EasyScriptStmtNode {
    public BreakStmtNode(SourceSection sourceSection) {
        super(sourceSection);
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        throw new BreakException();
    }
}
