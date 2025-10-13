package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * A debugger scope for the first level of a user-defined function.
 * It will contain the function arguments and local variables from that block.
 */
@ExportLibrary(InteropLibrary.class)
public final class FuncDebuggerScopeObject extends AbstractDebuggerScopeObject {
    private final UserFuncBodyStmtNode userFuncBodyStmtNode;

    public FuncDebuggerScopeObject(UserFuncBodyStmtNode userFuncBodyStmtNode, Frame frame) {
        super(frame);
        this.userFuncBodyStmtNode = userFuncBodyStmtNode;
    }

    @Override
    protected RefObject[] getReferences() {
        return this.userFuncBodyStmtNode.getFuncArgAndLocalVarRefs();
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.userFuncBodyStmtNode.getRootNode().getName();
    }
}
