package com.endoflineblog.truffle.part_06.nodes;

import com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * The {@link RootNode} for our built-in functions.
 * Simply wraps the Node representing the body of the function.
 */
public final class FunctionRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode functionBodyExpr;

    public FunctionRootNode(EasyScriptTruffleLanguage truffleLanguage,
            EasyScriptExprNode functionBodyExpr) {
        super(truffleLanguage);
        this.functionBodyExpr = functionBodyExpr;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.functionBodyExpr.executeGeneric(frame);
    }
}
