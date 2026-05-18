package com.endoflineblog.truffle.part_16.nodes.exprs.frame;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An implementation of {@link AbstractFrameGetNode}
 * that returns the parent {@link Frame}.
 * Because of how closures are implemented in
 * {@link com.endoflineblog.truffle.part_16.nodes.exprs.functions.FunctionDispatchNode},
 * we know that the materialized parent frame is kept in the argument with index 1.
 * We use a child instance of {@link AbstractFrameGetNode},
 * instead of referencing the provided {@link VirtualFrame},
 * so that we can support arbitrary levels of nesting functions within each other.
 */
public final class ParentFrameGetNode extends AbstractFrameGetNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private AbstractFrameGetNode currentOrParentFrameGetNode;

    public ParentFrameGetNode(AbstractFrameGetNode currentOrParentFrameGetNode) {
        this.currentOrParentFrameGetNode = currentOrParentFrameGetNode;
    }

    @Override
    public Frame executeFrame(VirtualFrame frame) {
        return (MaterializedFrame) this.currentOrParentFrameGetNode.executeFrame(frame).getArguments()[1];
    }
}
