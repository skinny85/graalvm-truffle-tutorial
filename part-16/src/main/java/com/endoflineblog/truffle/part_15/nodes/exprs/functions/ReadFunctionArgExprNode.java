package com.endoflineblog.truffle.part_15.nodes.exprs.functions;

import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An expression Node that represents referencing a given argument of a function -
 * either built-in, or user-defined.
 * Identical to the class with the same name from part 14.
 */
public final class ReadFunctionArgExprNode extends EasyScriptExprNode {
    private final int index;

    public ReadFunctionArgExprNode(int index) {
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // we are guaranteed the argument array has enough elements,
        // because of the logic in FunctionDispatchNode
        return frame.getArguments()[this.index];
    }
}
