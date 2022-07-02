package com.endoflineblog.truffle.part_08.nodes.root;

import com.endoflineblog.truffle.part_08.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_08.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * A {@link RootNode} that represents the execution of a block of statements.
 * Used as the {@link RootNode} for the entire EasyScript program,
 * and also for user-defined functions.
 */
public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode blockStmt;

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, BlockStmtNode blockStmt) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt);
    }

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, UserFuncBodyStmtNode blockStmt) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt);
    }

    private StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, EasyScriptStmtNode blockStmt) {
        super(truffleLanguage, frameDescriptor);

        this.blockStmt = blockStmt;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }
}
