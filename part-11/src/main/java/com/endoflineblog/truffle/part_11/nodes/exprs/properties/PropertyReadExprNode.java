package com.endoflineblog.truffle.part_11.nodes.exprs.properties;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 * Similar to the class with the same name from part 10,
 * the only difference is that we moved most of the functionality to the
 * {@link ObjectPropertyReadNode} class, in order to reduce the duplication between this class,
 * and {@link com.endoflineblog.truffle.part_11.nodes.exprs.arrays.ArrayIndexReadExprNode}.
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
