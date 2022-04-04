package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * A Node that represents the declaration of a variable or constant in EasyScript.
 * Identical to the class with the same name from part 5.
 *
 * @see #createVariable
 */
@NodeChild(value = "initializerExpr", type = EasyScriptExprNode.class)
@NodeField(name = "name", type = String.class)
@NodeField(name = "declarationKind", type = DeclarationKind.class)
public abstract class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    /*
     * We use these getters in EasyScriptRootNode,
     * that's why they're public.
     */

    public abstract EasyScriptExprNode getInitializerExpr();
    public abstract String getName();
    public abstract DeclarationKind getDeclarationKind();

    @Specialization
    protected Object createVariable(
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
