package com.endoflineblog.truffle.part_16.nodes.exprs.functions;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An expression Node that represents referencing a given argument of a function -
 * either built-in, or user-defined.
 * Very similar to the class with the same name from part 15,
 * the only difference is that we save the name of the argument this references.
 * We use that when searching for function argument references in
 * {@link com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode}.
 */
public final class ReadFunctionArgExprNode extends EasyScriptExprNode {
    /**
     * We reference this field in {@link com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode},
     * so we need to make it {@code public}.
     */
    public final int index;

    public final String argName;

    public ReadFunctionArgExprNode(int index, String argName) {
        this.index = index;
        this.argName = argName;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // we are guaranteed the argument array has enough elements,
        // because of the logic in FunctionDispatchNode
        return frame.getArguments()[this.index];
    }
}
