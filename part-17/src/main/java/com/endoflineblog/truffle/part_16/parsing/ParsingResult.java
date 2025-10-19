package com.endoflineblog.truffle.part_16.parsing;

import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.BlockStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

/**
 * The class used as the result of parsing in
 * {@link EasyScriptTruffleParser#parse}.
 * Very similar to the class with the same name from part 15,
 * the only difference is the additional {@link SourceSection}
 * property that represents the source section for the entire program,
 * and which is passed to the {@link com.endoflineblog.truffle.part_16.nodes.root.StmtBlockRootNode}.
 */
public final class ParsingResult {
    /** The Node representing the list of statements that the EasyScript program consists of. */
    public final BlockStmtNode programStmtBlock;

    /**
     * The {@link FrameDescriptor} used for the Truffle program itself
     * (which can contain local variables in this part).
     */
//    public final FrameDescriptor topLevelFrameDescriptor;

    public final SourceSection programSourceSection;

    public ParsingResult(BlockStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor,
            SourceSection programSourceSection) {
        this.programStmtBlock = programStmtBlock;
//        this.topLevelFrameDescriptor = topLevelFrameDescriptor;
        this.programSourceSection = programSourceSection;
    }
}
