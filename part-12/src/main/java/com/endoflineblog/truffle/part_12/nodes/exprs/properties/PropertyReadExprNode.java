package com.endoflineblog.truffle.part_12.nodes.exprs.properties;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 * Identical to the class with the same name from part 11.
 */
@NodeChild("targetExpr")
@NodeField(name = "propertyName", type = String.class)
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    protected abstract String getPropertyName();

    @Specialization
    protected Object readProperty(Object target,
            @Cached ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target, this.getPropertyName());
    }
}
