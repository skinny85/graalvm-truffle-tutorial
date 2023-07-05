package com.endoflineblog.truffle.part_09.nodes.stmts.variables;

import com.endoflineblog.truffle.part_09.common.DeclarationKind;
import com.endoflineblog.truffle.part_09.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * A Node that represents the declaration of a global
 * (as opposed to local to a function) variable or constant in EasyScript.
 * Very similar to the class with the same name from part 8,
 * the main difference is that we check whether a given global variable already exists
 * only the first time the Node is executed,
 * to correctly handle the same Truffle AST being executed multiple times
 * (which happens when you {@link org.graalvm.polyglot.Context#eval} the same program multiple times,
 * to save on the cost of parsing).
 */
@NodeChild(value = "initializerExpr", type = EasyScriptExprNode.class)
@NodeField(name = "name", type = String.class)
@NodeField(name = "declarationKind", type = DeclarationKind.class)
public abstract class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    protected abstract String getName();
    protected abstract DeclarationKind getDeclarationKind();

    @CompilationFinal
    private boolean checkVariableExists = true;

    @Specialization
    protected Object createVariable(Object value) {
        String variableId = this.getName();
        var context = this.currentLanguageContext();
        boolean isConst = this.getDeclarationKind() == DeclarationKind.CONST;
        boolean variableAlreadyExists = !context.globalScopeObject.newVariable(variableId, value, isConst);

        if (this.checkVariableExists) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.checkVariableExists = false;

            if (variableAlreadyExists) {
                throw new EasyScriptException(this, "Identifier '" + variableId + "' has already been declared");
            }
        }

        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
