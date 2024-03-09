package com.endoflineblog.truffle.part_13.nodes.root;

import com.endoflineblog.truffle.part_13.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_13.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_13.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_13.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * A {@link RootNode} that represents the execution of a block of statements.
 * Used as the {@link RootNode} for the entire EasyScript program,
 * and also for user-defined functions.
 * Identical to the class with the same name from part 12.
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
