package com.endoflineblog.truffle.part_15.nodes.root;

import com.endoflineblog.truffle.part_15.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_15.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_15.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_15.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * A {@link RootNode} that represents the execution of a block of statements.
 * Used as the {@link RootNode} for the entire EasyScript program,
 * and also for user-defined functions.
 * Almost identical to the class with the same name from part 14,
 * the only difference is the addition of the {@link #name} field,
 * which is populated with the function's name from
 * {@link com.endoflineblog.truffle.part_15.nodes.stmts.variables.FuncDeclStmtNode}
 * (or the {@code ":program"} string for the main level script in
 * {@link EasyScriptTruffleLanguage}),
 * and then used in the override of the {@link RootNode#getName()}
 * method, which is called when a stack trace is generated.
 */
public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode blockStmt;

    private final String name;

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, BlockStmtNode blockStmt, String name) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt, name);
    }

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, UserFuncBodyStmtNode blockStmt, String name) {
        this(truffleLanguage, frameDescriptor, (EasyScriptStmtNode) blockStmt, name);
    }

    private StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            FrameDescriptor frameDescriptor, EasyScriptStmtNode blockStmt, String name) {
        super(truffleLanguage, frameDescriptor);

        this.blockStmt = blockStmt;
        this.name = name;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
