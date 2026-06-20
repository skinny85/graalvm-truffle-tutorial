package com.endoflineblog.truffle.part_17.nodes.exprs.functions;

import com.endoflineblog.truffle.part_17.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_17.nodes.exprs.frame.AbstractFrameGetNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An expression Node that represents an assignment to a function argument.
 * Very similar to the class with the same name from part 16,
 * the only difference is the {@link AbstractFrameGetNode}
 * that gets used for function arguments in closures.
 */
public final class WriteFunctionArgExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private AbstractFrameGetNode currentOrParentFrameGetNode;

    private final int index;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;

    public WriteFunctionArgExprNode(AbstractFrameGetNode currentOrParentFrameGetNode,
                EasyScriptExprNode initializerExpr, int index) {
        this.currentOrParentFrameGetNode = currentOrParentFrameGetNode;
        this.index = index;
        this.initializerExpr = initializerExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object value = this.initializerExpr.executeGeneric(frame);
        Frame currentOrParentFrame = this.currentOrParentFrameGetNode.executeFrame(frame);
        // we are guaranteed the argument array has enough elements,
        // because of the logic in FunctionDispatchNode
        currentOrParentFrame.getArguments()[this.index] = value;
        return value;
    }
}
