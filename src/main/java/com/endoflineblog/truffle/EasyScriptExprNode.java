package com.endoflineblog.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class EasyScriptExprNode extends Node {
    public abstract int executeInt(VirtualFrame frame);
}
