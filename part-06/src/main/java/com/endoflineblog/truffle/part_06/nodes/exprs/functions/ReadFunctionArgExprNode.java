package com.endoflineblog.truffle.part_06.nodes.exprs.functions;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_06.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

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
        return index < arguments.length ? arguments[index] : Undefined.INSTANCE;
    }
}
