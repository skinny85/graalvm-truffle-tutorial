package com.endoflineblog.truffle.part_16.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_16.exceptions.BreakException;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node representing the {@code break} statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we add a {@link SourceSection}
 * parameter to the constructor, which we pass to the constructor of the superclass,
 * {@link EasyScriptStmtNode}.
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
