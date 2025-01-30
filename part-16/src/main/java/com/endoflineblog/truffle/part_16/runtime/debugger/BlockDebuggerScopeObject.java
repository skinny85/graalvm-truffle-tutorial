package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.endoflineblog.truffle.part_16.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * A debugger scope for a block statement.
 * This can be either any block except the top-level block of a user-defined function
 * (which is handled by {@link FuncDebuggerScopeObject}),
 * or any block (including the top-level one) in the main program.
 */
@ExportLibrary(InteropLibrary.class)
public final class BlockDebuggerScopeObject extends AbstractDebuggerScopeObject {
    // needs to be package-private,
    // as it's used in @Cached expressions
    final BlockStmtNode blockStmtNode;

    public BlockDebuggerScopeObject(BlockStmtNode blockStmtNode, Frame frame) {
        super(frame);
        this.blockStmtNode = blockStmtNode;
    }

    @Override
    protected RefObject[] getReferences() {
        return this.blockStmtNode.getLocalVarRefs();
    }

    @ExportMessage
    Object toDisplayString(
            @SuppressWarnings("unused") boolean allowSideEffects,
            @Cached(value = "this.blockStmtNode.findParentBlock()", adopt = false, allowUncached = true) @Shared("nodeGrandParentBlock") Node nodeGrandParentBlock
    ) {
        if (nodeGrandParentBlock instanceof RootNode) {
            return ((RootNode) nodeGrandParentBlock).getName();
        } else {
            return "block";
        }
    }

    @ExportMessage
    boolean hasScopeParent(
            @Cached(value = "this.blockStmtNode.findParentBlock()", adopt = false, allowUncached = true) @Shared("nodeGrandParentBlock") Node nodeGrandParentBlock) {
        return !(nodeGrandParentBlock instanceof StmtBlockRootNode);
    }

    @ExportMessage
    Object getScopeParent(
            @Cached(value = "this.blockStmtNode.findParentBlock()", adopt = false, allowUncached = true) @Shared("nodeGrandParentBlock") Node nodeGrandParentBlock)
            throws UnsupportedMessageException {
        if (nodeGrandParentBlock instanceof BlockStmtNode) {
            return new BlockDebuggerScopeObject((BlockStmtNode) nodeGrandParentBlock, this.frame);
        } else if (nodeGrandParentBlock instanceof UserFuncBodyStmtNode) {
            return new FuncDebuggerScopeObject((UserFuncBodyStmtNode) nodeGrandParentBlock, this.frame);
        } else {
            throw UnsupportedMessageException.create();
        }
    }
}
