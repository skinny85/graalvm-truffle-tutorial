package com.endoflineblog.truffle.part_16.nodes.exprs.frame;

import com.endoflineblog.truffle.part_16.nodes.EasyScriptNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class AbstractFrameGetNode extends EasyScriptNode {
    public abstract Frame executeFrame(VirtualFrame frame);
}
