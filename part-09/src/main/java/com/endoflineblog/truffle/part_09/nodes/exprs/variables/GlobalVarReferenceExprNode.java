package com.endoflineblog.truffle.part_09.nodes.exprs.variables;

import com.endoflineblog.truffle.part_09.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_09.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_09.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_09.runtime.FunctionObject;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
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

    @CompilationFinal
    private FunctionObject cachedFunction = null;

    @Specialization
    protected Object readVariable(
            @CachedContext(EasyScriptTruffleLanguage.class) EasyScriptLanguageContext context) {
        // consult the cache ("fast path" if it's populated)
        if (this.cachedFunction != null) {
            return this.cachedFunction;
        }

        // look up the variable in the global scope map ("slow path")...
        String variableId = this.getName();
        var value = context.globalScopeObject.getVariable(variableId);
        if (value == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        }

        // ...and populate the cache if it's a function
        if (value instanceof FunctionObject) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.cachedFunction = (FunctionObject) value;
        }

        return value;
    }
}
