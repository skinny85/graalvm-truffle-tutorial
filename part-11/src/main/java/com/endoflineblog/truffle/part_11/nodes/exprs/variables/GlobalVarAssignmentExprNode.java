package com.endoflineblog.truffle.part_11.nodes.exprs.variables;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_11.nodes.stmts.variables.GlobalVarDeclStmtNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
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

    @Specialization(limit = "1")
    protected Object assignVariable(DynamicObject globalScopeObject, Object value,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        String variableId = this.getName();
        Property property = objectLibrary.getProperty(globalScopeObject, variableId);
        if (property == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        if (property.getFlags() == 1) {
            // this is a constant
            Object existingValue = property.get(globalScopeObject, true);
            if (existingValue != GlobalVarDeclStmtNode.DUMMY) {
                // the first assignment to a constant is fine
                throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
            }
        }
        objectLibrary.put(globalScopeObject, variableId, value);
        return value;
    }
}
