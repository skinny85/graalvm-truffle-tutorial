package com.endoflineblog.truffle.part_08.nodes.exprs.variables;

import com.endoflineblog.truffle.part_08.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_08.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_08.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.CachedContext;
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
    protected Object readVariable(
            @CachedContext(EasyScriptTruffleLanguage.class) EasyScriptLanguageContext context) {
        String variableId = this.getName();
        var value = context.globalScopeObject.getVariable(variableId);
        if (value == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }
        return value;
    }
}
