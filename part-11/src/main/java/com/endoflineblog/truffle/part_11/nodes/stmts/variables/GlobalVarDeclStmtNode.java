package com.endoflineblog.truffle.part_11.nodes.stmts.variables;

import com.endoflineblog.truffle.part_11.common.DeclarationKind;
import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_11.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the declaration of a global
 * (as opposed to local to a function) variable or constant in EasyScript.
 * Identical to the class with the same name from part 10.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeField(name = "variableId", type = String.class)
@NodeField(name = "declarationKind", type = DeclarationKind.class)
public abstract class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    public static final Object DUMMY = new TruffleObject() {
        @Override
        public String toString() {
            return "Dummy";
        }
    };

    protected abstract String getVariableId();
    protected abstract DeclarationKind getDeclarationKind();

    @CompilationFinal
    private boolean checkVariableExists = true;

    @Specialization(limit = "1")
    protected Object declareVariable(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        var variableId = this.getVariableId();

        if (this.checkVariableExists) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.checkVariableExists = false;

            if (objectLibrary.containsKey(globalScopeObject, variableId)) {
                throw new EasyScriptException(this, "Identifier '" + variableId + "' has already been declared");
            }
        }

        var declarationKind = this.getDeclarationKind();
        Object initialValue = declarationKind == DeclarationKind.VAR
                // the default value for 'var' is 'undefined'
                ? Undefined.INSTANCE
                // for 'const' and 'let', we write a "dummy" value that we treat specially
                : DUMMY;
        int flags = declarationKind == DeclarationKind.CONST ? 1 : 0;
        objectLibrary.putWithFlags(globalScopeObject, variableId, initialValue, flags);

        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
