package com.endoflineblog.truffle.part_10.nodes.stmts.variables;

import com.endoflineblog.truffle.part_10.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_10.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_10.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_10.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_10.runtime.FunctionObject;
import com.endoflineblog.truffle.part_10.runtime.GlobalScopeObject;
import com.endoflineblog.truffle.part_10.runtime.Undefined;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the declaration of a function in EasyScript.
 * Similar to the class with the same name from part 8,
 * the only difference is that we add caching to the {@link CallTarget}
 * that is produced from the statements of the function body.
 * Additionally, since {@link FunctionObject} is now mutable,
 * we also cache the instance of {@link FunctionObject} that we get from the
 * {@link GlobalScopeObject},
 * and also call {@link FunctionObject#redefine}.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeField(name = "funcName", type = String.class)
@NodeField(name = "frameDescriptor", type = FrameDescriptor.class)
@NodeField(name = "funcBody", type = UserFuncBodyStmtNode.class)
@NodeField(name = "argumentCount", type = int.class)
public abstract class FuncDeclStmtNode extends EasyScriptStmtNode {
    protected abstract String getFuncName();
    protected abstract FrameDescriptor getFrameDescriptor();
    protected abstract UserFuncBodyStmtNode getFuncBody();
    protected abstract int getArgumentCount();

    @CompilationFinal
    private CallTarget cachedCallTarget;

    @CompilationFinal
    private FunctionObject cachedFunction;

    @Specialization(limit = "1")
    protected Object declareFunction(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        int argumentCount = this.getArgumentCount();

        if (this.cachedCallTarget == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();

            var truffleLanguage = this.currentTruffleLanguage();
            var funcRootNode = new StmtBlockRootNode(truffleLanguage, this.getFrameDescriptor(), this.getFuncBody());
            this.cachedCallTarget = Truffle.getRuntime().createCallTarget(funcRootNode);

            // we allow functions to be redefined, to comply with JavaScript semantics
            var funcName = this.getFuncName();
            Object existingVariable = objectLibrary.getOrDefault(globalScopeObject, funcName, null);
            // instanceof returns 'false' for null,
            // so this also covers the case when we're seeing this variable for the first time
            if (existingVariable instanceof FunctionObject) {
                FunctionObject existingFunction = (FunctionObject) existingVariable;
                existingFunction.redefine(this.cachedCallTarget, argumentCount);
                this.cachedFunction = existingFunction;
            } else {
                FunctionObject newFunction = new FunctionObject(funcName, this.cachedCallTarget, argumentCount);
                objectLibrary.putConstant(globalScopeObject, funcName, newFunction, 1);
                this.cachedFunction = newFunction;
            }
        }

        this.cachedFunction.redefine(this.cachedCallTarget, argumentCount);
        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
