package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class ObjectLiteralKeyValueConstantNode extends ObjectLiteralKeyValueNode {
    private final String key;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode value;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ObjectPropertyWriteNode objectPropertyWriteNode;

    public ObjectLiteralKeyValueConstantNode(String key, EasyScriptExprNode value) {
        this.key = key;
        this.value = value;
        this.objectPropertyWriteNode = ObjectPropertyWriteNodeGen.create();
    }

    public void executeObjectLiteralKeyValue(VirtualFrame frame, JavaScriptObject object) {
        Object value = this.value.executeGeneric(frame);
        this.objectPropertyWriteNode.executePropertyWrite(object, this.key, value);
    }
}
