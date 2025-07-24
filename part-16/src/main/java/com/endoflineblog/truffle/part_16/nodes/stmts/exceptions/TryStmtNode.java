package com.endoflineblog.truffle.part_16.nodes.stmts.exceptions;

import com.endoflineblog.truffle.part_16.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.BlockStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * This Node represents the implementation of the {@code try}-
 * {@code catch}-{@code finally} statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we need to add a {@link SourceSection}
 * parameter to its constructors,
 * since the {@link EasyScriptStmtNode} constructor now requires it.
 */
public final class TryStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode tryStatements;

    private final Integer exceptionVarFrameSlot;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode catchStatements;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode finallyStatements;

    public TryStmtNode(
            BlockStmtNode tryStatements, BlockStmtNode finallyStatements,
            SourceSection sourceSection) {
        this(tryStatements, null, null, finallyStatements, sourceSection);
    }

    public TryStmtNode(BlockStmtNode tryStatements, Integer exceptionVarFrameSlot,
            BlockStmtNode catchStatements, BlockStmtNode finallyStatements,
            SourceSection sourceSection) {
        super(sourceSection);
        this.tryStatements = tryStatements;
        this.exceptionVarFrameSlot = exceptionVarFrameSlot;
        this.catchStatements = catchStatements;
        this.finallyStatements = finallyStatements;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.exceptionVarFrameSlot == null) {
            try {
                return this.tryStatements.executeStatement(frame);
            } finally {
                this.finallyStatements.executeStatement(frame);
            }
        } else {
            try {
                return this.tryStatements.executeStatement(frame);
            } catch (EasyScriptException e) {
                frame.setObject(this.exceptionVarFrameSlot, e.value);
                return this.catchStatements.executeStatement(frame);
            } finally {
                if (this.finallyStatements != null) {
                    this.finallyStatements.executeStatement(frame);
                }
            }
        }
    }
}
