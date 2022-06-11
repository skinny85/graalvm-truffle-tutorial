package com.endoflineblog.truffle.part_08.nodes.stmts;

import com.endoflineblog.truffle.part_08.nodes.StmtBlockRootNode;
import com.endoflineblog.truffle.part_08.runtime.FunctionObject;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

/**
 * A Node that represents the declaration of a function in EasyScript.
 */
public final class FuncDeclStmtNode extends EasyScriptStmtNode {
    private final String funcName;
    private final FrameDescriptor frameDescriptor;
    private final int argumentCount;

    @SuppressWarnings("FieldMayBeFinal")
    @Node.Child
    private BlockStmtNode funcBody;

    public FuncDeclStmtNode(String funcName, FrameDescriptor frameDescriptor, BlockStmtNode funcBody, int argumentCount) {
        this.funcName = funcName;
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
        this.argumentCount = argumentCount;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        var truffleLanguage = this.currentTruffleLanguage();
        var funcRootNode = new StmtBlockRootNode(truffleLanguage, this.frameDescriptor, this.funcBody);
        var func = new FunctionObject(Truffle.getRuntime().createCallTarget(funcRootNode), this.argumentCount);

        var context = this.currentLanguageContext();
        // we allow functions to be redefined, to comply with JavaScript semantics
        context.globalScopeObject.newFunction(this.funcName, func);

        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
