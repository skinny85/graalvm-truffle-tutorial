package com.endoflineblog.truffle.part_13.nodes.exprs.properties;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp = 3}.
 * Very similar to {@link PropertyReadExprNode}.
 * Simply delegates to {@link CommonWritePropertyNode}.
 */
@NodeChild("targetExpr")
@NodeField(name = "propertyName", type = String.class)
@NodeChild("rvalueExpr")
public abstract class PropertyWriteExprNode extends EasyScriptExprNode {
    protected abstract EasyScriptExprNode getTargetExpr();
    protected abstract String getPropertyName();

    @Specialization
    protected Object writeProperty(VirtualFrame frame,
            Object target, Object rvalue,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        return commonWritePropertyNode.executeWriteProperty(
                this.getTargetExpr().evaluateAsThis(frame, target), this.getPropertyName(), rvalue);
    }
}
