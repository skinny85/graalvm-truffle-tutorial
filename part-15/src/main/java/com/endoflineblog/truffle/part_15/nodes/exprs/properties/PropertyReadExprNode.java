package com.endoflineblog.truffle.part_15.nodes.exprs.properties;

import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_15.nodes.exprs.objects.SuperExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 * Identical to the class with the same name from part 14.
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
        EasyScriptExprNode targetExpr = this.getTargetExpr();
        // if we're reading a property of 'super',
        // we know we need to look in its parent prototype,
        // and not in 'this' (which will be used as the method receiver)
        Object propertyTarget = targetExpr instanceof SuperExprNode
                ? ((SuperExprNode) targetExpr).readParentPrototype()
                : receiver;
        return this.readProperty(propertyTarget);
    }
}
