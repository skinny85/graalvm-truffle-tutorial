package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public final class ObjectLiteralKeyValueNode extends EasyScriptNode {
    private final String key;

    @Child
    @SuppressWarnings("FieldMayBeFinal")
    private EasyScriptExprNode value;

    @Child
    private DynamicObjectLibrary dynamicObjectLibrary;

    public ObjectLiteralKeyValueNode(String key, EasyScriptExprNode value) {
        this.key = key;
        this.value = value;
    }

    public void executeObjectWriteGeneric(VirtualFrame frame, JavaScriptObject object) {
        if (this.dynamicObjectLibrary == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.dynamicObjectLibrary = this.insert(DynamicObjectLibrary.getFactory().createDispatched(1));
        }

        Object value = this.value.executeGeneric(frame);
        this.dynamicObjectLibrary.putWithFlags(object, this.key, value, 0);
    }
}
