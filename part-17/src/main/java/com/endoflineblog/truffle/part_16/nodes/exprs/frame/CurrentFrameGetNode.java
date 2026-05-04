package com.endoflineblog.truffle.part_16.nodes.exprs.frame;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class CurrentFrameGetNode extends AbstractFrameGetNode {
    @Override
    public Frame executeFrame(VirtualFrame frame) {
        return frame;
    }
}
