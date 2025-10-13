package com.endoflineblog.truffle.part_16.nodes.root;

import com.endoflineblog.truffle.part_16.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A {@link RootNode} that represents the execution of a block of statements.
 * Used as the {@link RootNode} for the entire EasyScript program,
 * and also for user-defined functions.
 * Almost identical to the class with the same name from part 15,
 * the only difference is the addition of the {@link #sourceSection} field,
 * and returning it in the {@link #getSourceSection()} overridden method,
 * which represents the source of a given executable,
 * and which is needed for the debugger to be able to correctly inspect the language's source code.
 */
public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode blockStmt;

    private final String name;
    private final SourceSection sourceSection;

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, BlockStmtNode blockStmt) {
        this(truffleLanguage, frameDescriptor, blockStmt, null, null);
    }

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, BlockStmtNode blockStmt,
            String name, SourceSection sourceSection) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt, name, sourceSection);
    }

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, UserFuncBodyStmtNode blockStmt,
            String name) {
        this(truffleLanguage, frameDescriptor, blockStmt, name, blockStmt.getSourceSection());
    }

    private StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, EasyScriptStmtNode blockStmt,
            String name, SourceSection sourceSection) {
        super(truffleLanguage, frameDescriptor);

        this.blockStmt = blockStmt;
        this.name = name;
        this.sourceSection = sourceSection;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public SourceSection getSourceSection() {
        return this.sourceSection;
    }
}
