package com.endoflineblog.truffle.part_08.nodes.exprs.variables;

import com.endoflineblog.truffle.part_08.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * A Node that represents the expression of referencing a global variable in EasyScript.
 * Identical to the class with the same name from part 7.
 */
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization
    protected Object readVariable() {
        String variableId = this.getName();
        var value = this.currentLanguageContext().globalScopeObject.getVariable(variableId);
        if (value == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        return value;
    }
}
