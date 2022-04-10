package com.endoflineblog.truffle.part_07.nodes;

import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.stmts.BlockStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * The {@link RootNode} for our built-in functions.
 * Simply wraps the statement block Node representing the body of the function.
 */
public final class UserDefinedFuncRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode functionBody;

    public UserDefinedFuncRootNode(EasyScriptTruffleLanguage truffleLanguage,
            BlockStmtNode functionBody, FrameDescriptor frameDescriptor) {
        super(truffleLanguage, frameDescriptor);

        this.functionBody = functionBody;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.functionBody.executeStatement(frame);
    }
}
