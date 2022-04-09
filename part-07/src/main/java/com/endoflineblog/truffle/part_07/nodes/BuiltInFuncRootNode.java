package com.endoflineblog.truffle.part_07.nodes;

import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * The {@link RootNode} for our built-in functions.
 * Simply wraps the expression Node representing the body of the function.
 */
public final class BuiltInFuncRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode functionBodyExpr;

    public BuiltInFuncRootNode(EasyScriptTruffleLanguage truffleLanguage,
            EasyScriptExprNode functionBodyExpr) {
        super(truffleLanguage);

        this.functionBodyExpr = functionBodyExpr;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.functionBodyExpr.executeGeneric(frame);
    }
}
