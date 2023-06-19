package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public final class ObjectLiteralKeyValueNode extends EasyScriptNode {
    private final String key;

    @Child
    @SuppressWarnings("FieldMayBeFinal")
    private EasyScriptExprNode value;

    public ObjectLiteralKeyValueNode(String key, EasyScriptExprNode value) {
        this.key = key;
        this.value = value;
    }

    public void executeObjectWriteGeneric(VirtualFrame frame, JavaScriptObject object) {
        Object value = this.value.executeGeneric(frame);
        DynamicObjectLibrary.getUncached().putWithFlags(object, this.key, value, 0);
    }
}
