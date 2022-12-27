package com.endoflineblog.truffle.part_10.nodes.root;

import com.endoflineblog.truffle.part_10.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_10.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * The {@link RootNode} for our built-in functions.
 * Simply wraps the expression Node representing the body of the function.
 * Identical to the class with the same name from part 9.
 */
public final class BuiltInFuncRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BuiltInFunctionBodyExprNode functionBodyExpr;

    public BuiltInFuncRootNode(EasyScriptTruffleLanguage truffleLanguage,
            BuiltInFunctionBodyExprNode functionBodyExpr) {
        super(truffleLanguage);

        this.functionBodyExpr = functionBodyExpr;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.functionBodyExpr.executeGeneric(frame);
    }
}
