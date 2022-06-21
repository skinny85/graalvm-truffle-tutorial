package com.endoflineblog.truffle.part_08;

import com.endoflineblog.truffle.part_08.nodes.stmts.ProgramBlockStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import java.util.regex.Pattern;

public final class ParsingResult {
    private static final Pattern FRAME_SLOT_NAME_REGEX = Pattern.compile("-\\d+$");

    /**
     * To handle variables shadowing each other in nested scopes,
     * the parser adds `-<number>` to the end of each variable name when creating FrameSlots for them.
     * This method removes the suffix, and returns the original name of the variable.
     */
    public static String normalizeFrameSlotName(FrameSlot frameSlot) {
        return FRAME_SLOT_NAME_REGEX.matcher(frameSlot.getIdentifier().toString()).replaceAll("");
    }

    public final ProgramBlockStmtNode programStmtBlock;
    public final FrameDescriptor topLevelFrameDescriptor;

    public ParsingResult(ProgramBlockStmtNode programStmtBlock, FrameDescriptor topLevelFrameDescriptor) {
        this.programStmtBlock = programStmtBlock;
        this.topLevelFrameDescriptor = topLevelFrameDescriptor;
    }
}
