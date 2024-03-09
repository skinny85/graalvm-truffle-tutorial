package com.endoflineblog.truffle.part_13.nodes.exprs.objects;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node implementing the 'this' expression.
 */
public final class ThisExprNode extends EasyScriptExprNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // because of how we handle calls in FunctionDispatchNode,
        // the `this` object is always in the first argument
        return frame.getArguments()[0];
    }
}
