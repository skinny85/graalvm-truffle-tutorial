package com.endoflineblog.truffle.part_09.nodes.stmts.variables;

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
 * Very similar to the class from the same name from part 8,
 * the main difference is that we check whether a given global variable already exists
 * only the first time the Node is executed,
 * to correctly handle the same Truffle AST being executed multiple times
 * (which happens when you {@link org.graalvm.polyglot.Context#eval} the same program multiple times,
 * to save on the cost of parsing).
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
        var context = this.currentLanguageContext();
        boolean variableAlreadyExists = !context.globalScopeObject.newVariable(this.variableId, this.declarationKind);

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
