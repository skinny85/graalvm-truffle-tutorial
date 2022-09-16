package com.endoflineblog.truffle.part_09.nodes.stmts.variables;

import com.endoflineblog.truffle.part_09.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_09.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_09.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a function in EasyScript.
 * Identical to the class with the same name from part 7.
 */
public final class FuncDeclStmtNode extends EasyScriptStmtNode {
    private final String funcName;
    private final FrameDescriptor frameDescriptor;
    private final int argumentCount;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private UserFuncBodyStmtNode funcBody;

    @CompilationFinal
    private boolean executed;

    public FuncDeclStmtNode(String funcName, FrameDescriptor frameDescriptor, UserFuncBodyStmtNode funcBody, int argumentCount) {
        this.funcName = funcName;
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
        this.argumentCount = argumentCount;
        this.executed = false;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (!this.executed) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.executed = true;

            var truffleLanguage = this.currentTruffleLanguage();
            var funcRootNode = new StmtBlockRootNode(truffleLanguage, this.frameDescriptor, this.funcBody);
            CallTarget funcCallTarget = Truffle.getRuntime().createCallTarget(funcRootNode);
            var context = this.currentLanguageContext();
            // we allow functions to be redefined, to comply with JavaScript semantics
            context.globalScopeObject.registerFunction(this.funcName, funcCallTarget, this.argumentCount);
        }

        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
