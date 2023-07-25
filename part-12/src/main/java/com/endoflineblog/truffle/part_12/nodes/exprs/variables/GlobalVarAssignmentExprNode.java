package com.endoflineblog.truffle.part_12.nodes.exprs.variables;

import com.endoflineblog.truffle.part_12.common.Affix;
import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.EasyScriptBinaryOperationNode;
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
@NodeField(name = "affix", type = Affix.class)
public abstract class GlobalVarAssignmentExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    protected EasyScriptBinaryOperationNode operation;

    protected GlobalVarAssignmentExprNode(EasyScriptBinaryOperationNode operation) {
        this.operation = operation;
    }

    protected abstract String getName();

    protected abstract Affix getAffix();

    @Specialization(limit = "1")
    protected Object assignVariable(DynamicObject globalScopeObject, Object rvalue,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        String variableId = this.getName();
        Property property = objectLibrary.getProperty(globalScopeObject, variableId);
        if (property == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        if (property.getFlags() == 1) {
            throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
        }
        if (this.operation == null) {
            objectLibrary.put(globalScopeObject, variableId, rvalue);
            return rvalue;
        } else {
            Object prevValue = objectLibrary.getOrDefault(globalScopeObject, variableId, null);
            Object newValue = this.operation.executeOperation(prevValue, rvalue);
            objectLibrary.put(globalScopeObject, variableId, newValue);
            return this.getAffix() == Affix.POSTFIX ? prevValue : newValue;
        }
    }
}
