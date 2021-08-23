package com.endoflineblog.truffle.part_05.nodes.stmts;

import com.endoflineblog.truffle.part_05.EasyScriptException;
import com.endoflineblog.truffle.part_05.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_05.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_05.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * A Node that represents the declaration of a variable or constant in EasyScript.
 * Simply delegates to the assignment expression.
 */
@NodeChild(value = "initializerExpr", type = EasyScriptExprNode.class)
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    protected abstract String getName();

    /**
     * The sensible thing here would probably to return `undefined`,
     * but since we don't have it in the language yet,
     * simply return the value of the variable instead.
     */
    @Specialization
    protected Object assignVariable(
            Object value,
            @CachedContext(EasyScriptTruffleLanguage.class) EasyScriptLanguageContext context) {
        String variableId = this.getName();
        if (!context.globalScopeObject.newValue(variableId, value)) {
            throw new EasyScriptException("Identifier '" + variableId + "' has already been declared");
        }
        return value;
    }
}
