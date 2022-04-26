package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a global
 * (as opposed to local to a function) variable or constant in EasyScript.
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
            // ToDo apparently, this check should only happen for let & const, not var
            throw new EasyScriptException(this, "Identifier '" + variableId + "' has already been declared");
        }
        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
