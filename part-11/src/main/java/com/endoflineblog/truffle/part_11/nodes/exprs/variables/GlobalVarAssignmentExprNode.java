package com.endoflineblog.truffle.part_11.nodes.exprs.variables;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.GlobalScopeObjectExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;

/**
 * A Node that represents the expression of assigning a value to a global variable in EasyScript.
 * Identical to the class with the same name from part 10.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeChild(value = "assignmentExpr")
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization
    protected Object assignVariable(DynamicObject globalScopeObject, Object value,
            @Cached DynamicObject.GetPropertyNode getPropertyNode,
            @Cached DynamicObject.PutNode putNode) {
        String variableId = this.getName();
        Property property = getPropertyNode.execute(globalScopeObject, variableId);
        if (property == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        if (property.getFlags() == 1) {
            throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
        }
        putNode.execute(globalScopeObject, variableId, value);
        return value;
    }
}
