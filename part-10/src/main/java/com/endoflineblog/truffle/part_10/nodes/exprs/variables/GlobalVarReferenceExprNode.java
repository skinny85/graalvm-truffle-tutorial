package com.endoflineblog.truffle.part_10.nodes.exprs.variables;

import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_10.nodes.stmts.variables.GlobalVarDeclStmtNode;
import com.endoflineblog.truffle.part_10.runtime.FunctionObject;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the expression of referencing a global variable in EasyScript.
 * Very similar to the class with the same name from part 8,
 * the only difference is, since {@link FunctionObject} is now mutable,
 * we add caching references to variables that resolve to functions.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    /** The cached reference to a function that is used if this variable refers to a function. */
//    @CompilationFinal
//    private FunctionObject cachedFunction = null;

    @Specialization(limit = "1")
    protected Object readVariable(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        // consult the cache ("fast path" if it's populated)
//        if (this.cachedFunction != null) {
//            return this.cachedFunction;
//        }

        // look up the variable in the global scope map ("slow path")...
        String variableId = this.getName();
        var value = objectLibrary.getOrDefault(globalScopeObject, variableId, null);
        if (value == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        } else if (value == GlobalVarDeclStmtNode.DUMMY) {
            throw new EasyScriptException("Cannot access '" + variableId + "' before initialization");
        }

        // ...and populate the cache if it's a function
//        if (value instanceof FunctionObject) {
//            CompilerDirectives.transferToInterpreterAndInvalidate();
//            this.cachedFunction = (FunctionObject) value;
//        }

        return value;
    }
}
