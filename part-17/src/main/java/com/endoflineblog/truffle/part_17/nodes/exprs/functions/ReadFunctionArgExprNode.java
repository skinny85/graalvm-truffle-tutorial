package com.endoflineblog.truffle.part_17.nodes.exprs.functions;

import com.endoflineblog.truffle.part_17.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_17.nodes.exprs.frame.AbstractFrameGetNode;
import com.endoflineblog.truffle.part_17.nodes.exprs.frame.CurrentFrameGetNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An expression Node that represents referencing a given argument of a function -
 * either built-in, or user-defined.
 * Very similar to the class with the same name from part 16,
 * the only difference is the {@link AbstractFrameGetNode}
 * that gets used for local variables in closures.
 */
public final class ReadFunctionArgExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private AbstractFrameGetNode currentOrParentFrameGetNode;

    /**
     * We reference this field in {@link com.endoflineblog.truffle.part_17.nodes.stmts.blocks.UserFuncBodyStmtNode},
     * so we need to make it {@code public}.
     */
    public final int index;

    public final String argName;

    public ReadFunctionArgExprNode(int index, String argName) {
        this(new CurrentFrameGetNode(), index, argName);
    }

    public ReadFunctionArgExprNode(AbstractFrameGetNode currentOrParentFrameGetNode, int index, String argName) {
        this.currentOrParentFrameGetNode = currentOrParentFrameGetNode;
        this.index = index;
        this.argName = argName;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Frame currentOrParentFrame = this.currentOrParentFrameGetNode.executeFrame(frame);
        // we are guaranteed the argument array has enough elements,
        // because of the logic in FunctionDispatchNode
        return currentOrParentFrame.getArguments()[this.index];
    }
}
