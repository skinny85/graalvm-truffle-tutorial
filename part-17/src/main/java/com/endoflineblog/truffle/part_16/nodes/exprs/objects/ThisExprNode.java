package com.endoflineblog.truffle.part_16.nodes.exprs.objects;

import com.endoflineblog.truffle.part_16.nodes.exprs.functions.AbstractFuncMemberReadNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node implementing the 'this' expression.
 * Identical to the class with the same name from part 15.
 */
public final class ThisExprNode extends AbstractFuncMemberReadNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // because of how we handle calls in FunctionDispatchNode,
        // the `this` object is always in the first argument
        return frame.getArguments()[0];
    }
}
