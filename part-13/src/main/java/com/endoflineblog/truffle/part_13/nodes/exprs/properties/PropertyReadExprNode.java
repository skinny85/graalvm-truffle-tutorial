package com.endoflineblog.truffle.part_13.nodes.exprs.properties;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 */
@NodeChild("targetExpr")
@NodeField(name = "propertyName", type = String.class)
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    protected abstract EasyScriptExprNode getTargetExpr();
    protected abstract String getPropertyName();

    @Child
    private CommonReadPropertyNode commonReadPropertyNode;

    @Specialization
    protected Object readProperty(Object target) {
        return this.getOrCreateCommonReadPropertyNode().executeReadProperty(
                target, this.getPropertyName());
    }

    @Override
    public Object evaluateAsReceiver(VirtualFrame frame) {
        return this.getTargetExpr().executeGeneric(frame);
    }

    @Override
    public Object evaluateAsFunction(VirtualFrame frame, Object receiver) {
        return this.readProperty(receiver);
    }

    private CommonReadPropertyNode getOrCreateCommonReadPropertyNode() {
        if (this.commonReadPropertyNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.commonReadPropertyNode = CommonReadPropertyNodeGen.create();
            this.insert(commonReadPropertyNode);
        }
        return this.commonReadPropertyNode;
    }
}
