package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class ObjectLiteralKeyValueNode extends EasyScriptNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode keyExpr, valueExpr;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ObjectPropertyWriteNode objectPropertyWriteNode;

    public ObjectLiteralKeyValueNode(EasyScriptExprNode keyExpr, EasyScriptExprNode valueExpr) {
        this.keyExpr = keyExpr;
        this.valueExpr = valueExpr;
        this.objectPropertyWriteNode = ObjectPropertyWriteNodeGen.create();
    }

    public void executeObjectLiteralKeyValue(VirtualFrame frame, JavaScriptObject object) {
        Object key = this.keyExpr.executeGeneric(frame);
        Object value = this.valueExpr.executeGeneric(frame);
        this.objectPropertyWriteNode.executePropertyWrite(object, key, value);
    }
}
