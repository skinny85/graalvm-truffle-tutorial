package com.endoflineblog.truffle.part_14.nodes.exprs.properties;

import com.endoflineblog.truffle.part_14.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp = 3}.
 * Very similar to {@link PropertyReadExprNode}.
 * Simply delegates to {@link CommonWritePropertyNode}.
 * Identical to the class with the same name from part 13.
 */
@NodeChild("targetExpr")
@NodeField(name = "propertyName", type = String.class)
@NodeChild("rvalueExpr")
public abstract class PropertyWriteExprNode extends EasyScriptExprNode {
    protected abstract String getPropertyName();

    @Specialization
    protected Object writeProperty(Object target, Object rvalue,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        return commonWritePropertyNode.executeWriteProperty(target, this.getPropertyName(), rvalue);
    }
}
