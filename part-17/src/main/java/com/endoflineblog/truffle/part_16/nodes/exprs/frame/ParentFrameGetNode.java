package com.endoflineblog.truffle.part_16.nodes.exprs.frame;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

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
