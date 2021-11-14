package com.endoflineblog.truffle.part_06.nodes.exprs.functions;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_06.runtime.Undefined;
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
        Object[] arguments = frame.getArguments();
        // In JavaScript, it's legal to call a function with fewer arguments than it declares;
        // in that case, the arguments not provided are assigned 'undefined'
        return this.index < arguments.length ? arguments[this.index] : Undefined.INSTANCE;
    }
}
