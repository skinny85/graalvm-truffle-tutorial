package com.endoflineblog.truffle.part_08.nodes.exprs.variables;

import com.endoflineblog.truffle.part_08.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * A Node that represents the expression of assigning a value to a global variable in EasyScript.
 * Identical to the class with the same name from part 7.
 */
@NodeChild(value = "assignmentExpr")
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization
    protected Object assignVariable(Object value) {
        String variableId = this.getName();
        if (!this.currentLanguageContext().globalScopeObject.updateVariable(variableId, value)) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        return value;
    }
}
