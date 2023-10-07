package com.endoflineblog.truffle.part_13.nodes.exprs.objects;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

public class ThisExprNode extends EasyScriptExprNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return frame.getArguments().length > 0
                // if 'this' is available, it's passed as the first argument
                ? frame.getArguments()[0]
                // if 'this' is not available, it's 'undefined'
                : Undefined.INSTANCE;
    }
}
