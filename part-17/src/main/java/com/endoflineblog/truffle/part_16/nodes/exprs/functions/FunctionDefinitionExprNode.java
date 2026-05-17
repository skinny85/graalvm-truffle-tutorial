package com.endoflineblog.truffle.part_16.nodes.exprs.functions;

import com.endoflineblog.truffle.part_16.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_16.runtime.FunctionObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class FunctionDefinitionExprNode extends EasyScriptExprNode {
    private final FrameDescriptor frameDescriptor;
    private final UserFuncBodyStmtNode funcBody;
    private final String funcName;
    private final int argumentCount;
    private final boolean isClosure;

    @CompilationFinal
    private CallTarget cachedCallTarget;

    @CompilationFinal
    private FunctionObject cachedFunction;

    public FunctionDefinitionExprNode(FrameDescriptor frameDescriptor, UserFuncBodyStmtNode funcBody,
            String funcName, int argumentCount, boolean isClosure) {
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
        this.funcName = funcName;
        this.argumentCount = argumentCount;
        this.isClosure = isClosure;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (this.cachedCallTarget == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();

            var truffleLanguage = this.currentTruffleLanguage();
            var funcRootNode = new StmtBlockRootNode(truffleLanguage,
                    this.frameDescriptor, this.funcBody, this.funcName);
            this.cachedCallTarget = funcRootNode.getCallTarget();

            if (!this.isClosure) {
                ShapesAndPrototypes shapesAndPrototypes = this.currentLanguageContext().shapesAndPrototypes;
                this.cachedFunction = new FunctionObject(shapesAndPrototypes.rootShape,
                        shapesAndPrototypes.functionPrototype,
                        this.cachedCallTarget, this.argumentCount);
            }
        }

        if (!this.isClosure) {
            return this.cachedFunction;
        } else {
            MaterializedFrame materializedFrame = frame.materialize();
            ShapesAndPrototypes shapesAndPrototypes = this.currentLanguageContext().shapesAndPrototypes;
            return new FunctionObject(shapesAndPrototypes.rootShape, shapesAndPrototypes.functionPrototype,
                    this.cachedCallTarget, this.argumentCount, materializedFrame);
        }
    }
}
