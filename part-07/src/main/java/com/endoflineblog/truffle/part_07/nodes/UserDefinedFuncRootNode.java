package com.endoflineblog.truffle.part_07.nodes;

import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.BlockStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * The {@link RootNode} for our built-in functions.
 * Simply wraps the Node representing the body of the function.
 */
public final class UserDefinedFuncRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode functionBody;

    public UserDefinedFuncRootNode(EasyScriptTruffleLanguage truffleLanguage,
            BlockStmtNode functionBody) {
        super(truffleLanguage);
        this.functionBody = functionBody;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.functionBody.executeStatement(frame);
    }
}
