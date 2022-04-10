package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.nodes.UserDefinedFuncRootNode;
import com.endoflineblog.truffle.part_07.runtime.FunctionObject;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a function in EasyScript.
 */
public final class FuncDeclStmtNode extends EasyScriptStmtNode {
    private final String funcName;
    private final FrameDescriptor frameDescriptor;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode funcBody;

    public FuncDeclStmtNode(String funcName, FrameDescriptor frameDescriptor, BlockStmtNode funcBody) {
        this.funcName = funcName;
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        var truffleLanguage = this.currentTruffleLanguage();
        var funcRootNode = new UserDefinedFuncRootNode(truffleLanguage, this.funcBody, this.frameDescriptor);
        var func = new FunctionObject(Truffle.getRuntime().createCallTarget(funcRootNode));

        var context = this.currentLanguageContext();
        if (!context.globalScopeObject.newConstant(this.funcName, func)) {
            throw new EasyScriptException(this, "Identifier '" + this.funcName + "' has already been declared");
        }

        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
