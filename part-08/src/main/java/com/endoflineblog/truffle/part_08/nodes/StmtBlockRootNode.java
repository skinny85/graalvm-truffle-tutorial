package com.endoflineblog.truffle.part_08.nodes;

import com.endoflineblog.truffle.part_08.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_08.nodes.stmts.BlockStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * A {@link RootNode} that represents the execution of a {@link BlockStmtNode block of statements}.
 * Used as the {@link RootNode} for the entire EasyScript program,
 * and also for user-defined functions.
 */
public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode blockStmt;

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            BlockStmtNode blockStmt) {
        this(truffleLanguage, null, blockStmt);
    }

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, BlockStmtNode blockStmt) {
        super(truffleLanguage, frameDescriptor);

        this.blockStmt = blockStmt;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }
}
