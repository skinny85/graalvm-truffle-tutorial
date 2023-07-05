package com.endoflineblog.truffle.part_10.nodes.exprs.variables;

import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.nodes.exprs.GlobalScopeObjectExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Property;

/**
 * A Node that represents the expression of assigning a value to a global variable in EasyScript.
 * Similar to the class with the same name from part 9,
 * the main difference is that we save the value of the variable
 * directly in the {@link com.endoflineblog.truffle.part_10.runtime.GlobalScopeObject}
 * (for getting a reference to which we use the {@link GlobalScopeObjectExprNode}),
 * using {@link DynamicObjectLibrary}.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeChild(value = "assignmentExpr")
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization(limit = "1")
    protected Object assignVariable(DynamicObject globalScopeObject, Object value,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        String variableId = this.getName();
        Property property = objectLibrary.getProperty(globalScopeObject, variableId);
        if (property == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        if (property.getFlags() == 1) {
            throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
        }
        objectLibrary.put(globalScopeObject, variableId, value);
        return value;
    }
}
