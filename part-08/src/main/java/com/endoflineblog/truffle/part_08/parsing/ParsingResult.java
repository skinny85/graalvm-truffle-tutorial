package com.endoflineblog.truffle.part_08.parsing;

import com.endoflineblog.truffle.part_08.nodes.stmts.blocks.BlockStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;

public final class ParsingResult {
    public final BlockStmtNode programStmtBlock;
    public final FrameDescriptor topLevelFrameDescriptor;

    public ParsingResult(BlockStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor) {
        this.programStmtBlock = programStmtBlock;
        this.topLevelFrameDescriptor = topLevelFrameDescriptor;
    }
}
