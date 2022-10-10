package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a global
 * (as opposed to local to a function) variable or constant in EasyScript.
 * Very similar to the class with the same name from part 6,
 * the only difference is that we no longer have the variable's initializer as a child expression;
 * instead, the initializer is executed in a separate
 * {@link com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarAssignmentExprNode}
 * (this is because user-defined functions, which we start supporting in this part,
 * can now potentially access a global variable before its initializer had a chance to run,
 * and we need to detect that error).
 */
public final class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    private final String variableId;
    private final DeclarationKind declarationKind;

    public GlobalVarDeclStmtNode(String variableId, DeclarationKind declarationKind) {
        this.variableId = variableId;
        this.declarationKind = declarationKind;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        EasyScriptLanguageContext context = this.currentLanguageContext();
        if (!context.globalScopeObject.newVariable(this.variableId, this.declarationKind)) {
            throw new EasyScriptException(this, "Identifier '" + this.variableId + "' has already been declared");
        }
        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
