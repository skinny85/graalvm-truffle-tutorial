package com.endoflineblog.truffle.part_14.nodes.stmts.variables;

import com.endoflineblog.truffle.part_14.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_14.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_14.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_14.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_14.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_14.runtime.FunctionObject;
import com.endoflineblog.truffle.part_14.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the declaration of a function in EasyScript.
 * Identical to the class with the same name from part 13.
 */
@NodeChild(value = "containerObjectExpr", type = EasyScriptExprNode.class)
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
    private FunctionObject cachedFunction;

    @Specialization(limit = "2")
    protected Object declareFunction(DynamicObject containerObject,
            @CachedLibrary("containerObject") DynamicObjectLibrary objectLibrary) {
        if (this.cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();

            var truffleLanguage = this.currentTruffleLanguage();
            var funcRootNode = new StmtBlockRootNode(truffleLanguage, this.getFrameDescriptor(), this.getFuncBody());
            var callTarget = funcRootNode.getCallTarget();

            ShapesAndPrototypes shapesAndPrototypes = this.currentLanguageContext().shapesAndPrototypes;
            this.cachedFunction = new FunctionObject(shapesAndPrototypes.rootShape,
                    shapesAndPrototypes.functionPrototype, callTarget, this.getArgumentCount());
        }

        // we allow functions to be redefined, to comply with JavaScript semantics
        objectLibrary.putConstant(containerObject, this.getFuncName(), this.cachedFunction, 0);

        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
