package com.endoflineblog.truffle.part_14.nodes.exprs.functions;

import com.endoflineblog.truffle.part_14.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An expression Node that represents an assignment to a function argument.
 * Identical to the class with the same name from part 13.
 */
public final class WriteFunctionArgExprNode extends EasyScriptExprNode {
    private final int index;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;

    public WriteFunctionArgExprNode(EasyScriptExprNode initializerExpr, int index) {
        this.index = index;
        this.initializerExpr = initializerExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object value = this.initializerExpr.executeGeneric(frame);
        // we are guaranteed the argument array has enough elements,
        // because of the logic in FunctionDispatchNode
        frame.getArguments()[this.index] = value;
        return value;
    }
}
