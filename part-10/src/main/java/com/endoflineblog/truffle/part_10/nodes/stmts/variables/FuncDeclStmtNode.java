package com.endoflineblog.truffle.part_10.nodes.stmts.variables;

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
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

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
public final class FuncDeclStmtNode extends EasyScriptStmtNode {
    private final String funcName;
    private final FrameDescriptor frameDescriptor;
    private final int argumentCount;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private UserFuncBodyStmtNode funcBody;

    @CompilationFinal
    private CallTarget cachedCallTarget;

    @CompilationFinal
    private FunctionObject cachedFunction;

    public FuncDeclStmtNode(String funcName, FrameDescriptor frameDescriptor, UserFuncBodyStmtNode funcBody, int argumentCount) {
        this.funcName = funcName;
        this.frameDescriptor = frameDescriptor;
        this.funcBody = funcBody;
        this.argumentCount = argumentCount;
        this.cachedCallTarget = null;
        this.cachedFunction = null;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.cachedCallTarget == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();

            var truffleLanguage = this.currentTruffleLanguage();
            var funcRootNode = new StmtBlockRootNode(truffleLanguage, this.frameDescriptor, this.funcBody);
            this.cachedCallTarget = Truffle.getRuntime().createCallTarget(funcRootNode);
            var context = this.currentLanguageContext();
            // we allow functions to be redefined, to comply with JavaScript semantics
            this.cachedFunction = context.globalScopeObject.registerFunction(this.funcName, this.cachedCallTarget, this.argumentCount);
        }

        this.cachedFunction.redefine(this.cachedCallTarget, this.argumentCount);
        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
