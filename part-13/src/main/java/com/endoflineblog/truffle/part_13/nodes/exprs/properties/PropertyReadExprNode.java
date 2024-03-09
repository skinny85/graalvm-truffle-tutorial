package com.endoflineblog.truffle.part_13.nodes.exprs.properties;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 * Very similar to the class with the same name from part 12,
 * the main differences being overriding the new
 * {@link EasyScriptExprNode#evaluateAsReceiver} and
 * {@link EasyScriptExprNode#evaluateAsFunction} methods,
 * and thus changing the {@link #readProperty}
 * specialization to use {@link CommonReadPropertyNode}
 * through a {@link com.oracle.truffle.api.nodes.Node.Child} field,
 * instead of the {@link com.oracle.truffle.api.dsl.Cached} annotation,
 * since {@link #readProperty} is now invoked from {@link #evaluateAsFunction}.
 */
@NodeChild("targetExpr")
@NodeField(name = "propertyName", type = String.class)
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    protected abstract EasyScriptExprNode getTargetExpr();
    protected abstract String getPropertyName();

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private CommonReadPropertyNode commonReadPropertyNode = CommonReadPropertyNodeGen.create();

    @Specialization
    protected Object readProperty(Object target) {
        return this.commonReadPropertyNode.executeReadProperty(
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
}
