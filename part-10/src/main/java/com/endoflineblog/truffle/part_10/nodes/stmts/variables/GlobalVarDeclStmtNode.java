package com.endoflineblog.truffle.part_10.nodes.stmts.variables;

import com.endoflineblog.truffle.part_10.common.DeclarationKind;
import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_10.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_10.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * A Node that represents the declaration of a global
 * (as opposed to local to a function) variable or constant in EasyScript.
 * Similar to the class with the same name from part 9,
 * the main difference is that we save the initial value of the variable
 * directly in the {@link com.endoflineblog.truffle.part_10.runtime.GlobalScopeObject}
 * (for getting a reference to which we use the {@link GlobalScopeObjectExprNode}),
 * using {@link com.oracle.truffle.api.object.DynamicObject} property nodes.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeChild(value = "initializerExpr", type = EasyScriptExprNode.class)
@NodeField(name = "name", type = String.class)
@NodeField(name = "declarationKind", type = DeclarationKind.class)
public abstract class GlobalVarDeclStmtNode extends EasyScriptStmtNode {
    protected abstract String getName();
    protected abstract DeclarationKind getDeclarationKind();

    @CompilationFinal
    private boolean checkVariableExists = true;

    @Specialization
    protected Object createVariable(DynamicObject globalScopeObject, Object value,
            @Cached DynamicObject.ContainsKeyNode containsKeyNode,
            @Cached DynamicObject.PutNode putNode) {
        var variableId = this.getName();

        if (this.checkVariableExists) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.checkVariableExists = false;

            if (containsKeyNode.execute(globalScopeObject, variableId)) {
                throw new EasyScriptException(this, "Identifier '" + variableId + "' has already been declared");
            }
        }

        int flags = this.getDeclarationKind() == DeclarationKind.CONST ? 1 : 0;
        putNode.executeWithFlags(globalScopeObject, variableId, value, flags);

        // we return 'undefined' for statements that declare variables
        return Undefined.INSTANCE;
    }
}
