package com.endoflineblog.truffle.part_06.nodes.stmts;

import com.endoflineblog.truffle.part_06.DeclarationKind;
import com.endoflineblog.truffle.part_06.EasyScriptException;
import com.endoflineblog.truffle.part_06.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_06.runtime.Undefined;
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
@NodeField(name = "declarationKind", type = DeclarationKind.class)
public abstract class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    public abstract EasyScriptExprNode getInitializerExpr();
    public abstract String getName();
    public abstract DeclarationKind getDeclarationKind();

    @Specialization
    protected Object assignVariable(
            Object value,
            @CachedContext(EasyScriptTruffleLanguage.class) EasyScriptLanguageContext context) {
        String variableId = this.getName();
        boolean isConst = this.getDeclarationKind() == DeclarationKind.CONST;
        if (!context.globalScopeObject.newVariable(variableId, value, isConst)) {
            throw new EasyScriptException(this, "Identifier '" + variableId + "' has already been declared");
        }
        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
