package com.endoflineblog.truffle.part_15.nodes.stmts.exceptions;

import com.endoflineblog.truffle.part_15.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_15.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_15.nodes.stmts.blocks.BlockStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * This Node represents the implementation of the {@code try}-
 * {@code catch}-{@code finally} statement.
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

    public TryStmtNode(BlockStmtNode tryStatements, BlockStmtNode finallyStatements) {
        this(tryStatements, null, null, finallyStatements);
    }

    public TryStmtNode(BlockStmtNode tryStatements, Integer exceptionVarFrameSlot,
            BlockStmtNode catchStatements, BlockStmtNode finallyStatements) {
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
