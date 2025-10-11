package com.endoflineblog.truffle.part_16.nodes.stmts.variables;

import com.endoflineblog.truffle.part_16.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_16.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.endoflineblog.truffle.part_16.runtime.FunctionObject;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

@NodeField(name = "argIndex", type = int.class)
@NodeField(name = "funcName", type = String.class)
@NodeField(name = "funcNestingLevel", type = int.class)
@NodeField(name = "frameDescriptor", type = FrameDescriptor.class)
@NodeField(name = "funcBody", type = UserFuncBodyStmtNode.class)
@NodeField(name = "argumentCount", type = int.class)
public abstract class NestedFuncDeclStmtNode extends EasyScriptStmtNode {
    protected abstract int getArgIndex();
    protected abstract String getFuncName();
    protected abstract int getFuncNestingLevel();
    protected abstract FrameDescriptor getFrameDescriptor();
    protected abstract UserFuncBodyStmtNode getFuncBody();
    protected abstract int getArgumentCount();

    @CompilationFinal
    private CallTarget cachedCallTarget;

    protected NestedFuncDeclStmtNode() {
        super(null);
    }

    @Specialization(limit = "2")
    protected Object declareFunction(VirtualFrame frame,
            @CachedLibrary(limit = "2") DynamicObjectLibrary objectLibrary) {
        if (this.cachedCallTarget == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();

            var truffleLanguage = this.currentTruffleLanguage();
            var funcRootNode = new StmtBlockRootNode(truffleLanguage,
                    this.getFrameDescriptor(), this.getFuncBody(), this.getFuncName());
            this.cachedCallTarget = funcRootNode.getCallTarget();
        }

        var environment = (Environment) frame.getArguments()[this.getFuncNestingLevel()];
        ShapesAndPrototypes shapesAndPrototypes = this.currentLanguageContext().shapesAndPrototypes;
        var func = new FunctionObject(shapesAndPrototypes.rootShape, shapesAndPrototypes.functionPrototype,
                this.cachedCallTarget, this.getArgumentCount(), environment);
        objectLibrary.put(environment, this.getArgIndex(), func);

        return Undefined.INSTANCE;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return false;
    }
}
