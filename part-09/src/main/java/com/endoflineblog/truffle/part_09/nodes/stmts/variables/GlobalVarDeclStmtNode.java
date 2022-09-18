package com.endoflineblog.truffle.part_09.nodes.stmts.variables;

import com.endoflineblog.truffle.part_09.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_09.common.DeclarationKind;
import com.endoflineblog.truffle.part_09.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a global
 * (as opposed to local to a function) variable or constant in EasyScript.
 * Identical to the class with the same name from part 7.
 */
public final class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    private final String variableId;
    private final DeclarationKind declarationKind;

    @CompilationFinal
    private boolean checkVariableExists;

    public GlobalVarDeclStmtNode(String variableId, DeclarationKind declarationKind) {
        this.variableId = variableId;
        this.declarationKind = declarationKind;
        this.checkVariableExists = true;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        EasyScriptLanguageContext context = this.currentLanguageContext();
        boolean variableAlreadyExists = !context.globalScopeObject.newVariable(this.variableId, this.declarationKind, !this.checkVariableExists);
        if (this.checkVariableExists) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.checkVariableExists = false;

            if (variableAlreadyExists) {
                throw new EasyScriptException(this, "Identifier '" + this.variableId + "' has already been declared");
            }
        }
        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
