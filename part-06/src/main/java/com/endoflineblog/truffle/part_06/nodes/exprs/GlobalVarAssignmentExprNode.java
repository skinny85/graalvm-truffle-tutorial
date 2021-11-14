package com.endoflineblog.truffle.part_06.nodes.exprs;

import com.endoflineblog.truffle.part_06.EasyScriptException;
import com.endoflineblog.truffle.part_06.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * A Node that represents the expression of assigning a value to a global variable in EasyScript.
 * Identical to the class with the same name from part 5.
 */
@NodeChild(value = "assignmentExpr")
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization
    protected Object assignVariable(
            Object value,
            @CachedContext(EasyScriptTruffleLanguage.class) EasyScriptLanguageContext context) {
        String variableId = this.getName();
        if (!context.globalScopeObject.updateVariable(variableId, value)) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        return value;
    }
}
