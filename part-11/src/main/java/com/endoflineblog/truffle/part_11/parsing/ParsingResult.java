package com.endoflineblog.truffle.part_11.parsing;

import com.endoflineblog.truffle.part_11.nodes.stmts.blocks.BlockStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;

/**
 * The class used as the result of parsing in
 * {@link EasyScriptTruffleParser#parse}.
 * Identical to the class with the same name from part 10.
 */
public final class ParsingResult {
    /** The Node representing the list of statements that the EasyScript program consists of. */
    public final BlockStmtNode programStmtBlock;
    /**
     * The {@link FrameDescriptor} used for the Truffle program itself
     * (which can contain local variables in this part).
     */
    public final FrameDescriptor topLevelFrameDescriptor;

    public ParsingResult(BlockStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor) {
        this.programStmtBlock = programStmtBlock;
        this.topLevelFrameDescriptor = topLevelFrameDescriptor;
    }
}
