package com.endoflineblog.truffle.part_12.nodes.exprs.properties;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyWriteNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyWriteNodeGen;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;

public final class PropertyWriteExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode targetExpr;

    private final TruffleString propertyName;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode rvalueExpr;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ObjectPropertyWriteNode objectPropertyWriteNode;

    public PropertyWriteExprNode(EasyScriptExprNode targetExpr, String propertyName, EasyScriptExprNode rvalueExpr) {
        this.targetExpr = targetExpr;
        this.propertyName = EasyScriptTruffleStrings.fromJavaString(propertyName);
        this.rvalueExpr = rvalueExpr;
        this.objectPropertyWriteNode = ObjectPropertyWriteNodeGen.create();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object target = this.targetExpr.executeGeneric(frame);
        Object rvalue = this.rvalueExpr.executeGeneric(frame);
        return this.objectPropertyWriteNode.executePropertyWrite(target, this.propertyName, rvalue);
    }
}
