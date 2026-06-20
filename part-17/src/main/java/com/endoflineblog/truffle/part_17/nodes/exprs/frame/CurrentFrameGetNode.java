package com.endoflineblog.truffle.part_17.nodes.exprs.frame;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * An implementation of {@link AbstractFrameGetNode}
 * that just returns the current {@link VirtualFrame}.
 * Used for references to arguments and local variables inside a given function.
 */
public final class CurrentFrameGetNode extends AbstractFrameGetNode {
    @Override
    public Frame executeFrame(VirtualFrame frame) {
        return frame;
    }
}
