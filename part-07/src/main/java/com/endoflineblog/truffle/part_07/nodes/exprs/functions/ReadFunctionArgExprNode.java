package com.endoflineblog.truffle.part_07.nodes.exprs.functions;

import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An expression Node that represents referencing a given argument of a function.
 * For built-in functions,
 * we create one of these for each argument the function takes
 * (with the correct index, starting at 0),
 * and then add them as children of the function's body expression Node.
 * This way, we can write specializations in that function body expression Node
 * that receive the function arguments already computed.
 */
public final class ReadFunctionArgExprNode extends EasyScriptExprNode {
    private final int index;

    public ReadFunctionArgExprNode(int index) {
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // we are guaranteed the argument array has enough elements
        return frame.getArguments()[this.index];
    }
}
