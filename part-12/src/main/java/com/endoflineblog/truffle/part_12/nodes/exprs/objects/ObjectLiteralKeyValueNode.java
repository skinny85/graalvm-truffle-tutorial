package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class ObjectLiteralKeyValueNode extends EasyScriptNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode key, value;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ObjectPropertyWriteNode objectPropertyWriteNode;

    public ObjectLiteralKeyValueNode(EasyScriptExprNode key, EasyScriptExprNode value) {
        this.key = key;
        this.value = value;
        this.objectPropertyWriteNode = ObjectPropertyWriteNodeGen.create();
    }

    public void executeObjectLiteralKeyValue(VirtualFrame frame, JavaScriptObject object) {
        Object key = this.key.executeGeneric(frame);
        Object value = this.value.executeGeneric(frame);
        this.objectPropertyWriteNode.executePropertyWrite(object, key, value);
    }
}
