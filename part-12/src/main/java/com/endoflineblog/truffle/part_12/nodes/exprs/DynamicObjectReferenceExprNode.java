package com.endoflineblog.truffle.part_12.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;

public final class DynamicObjectReferenceExprNode extends EasyScriptExprNode {
    private final DynamicObject dynamicObject;

    public DynamicObjectReferenceExprNode(DynamicObject dynamicObject) {
        this.dynamicObject = dynamicObject;
    }

    @Override
    public DynamicObject executeGeneric(VirtualFrame frame) {
        return this.dynamicObject;
    }
}
