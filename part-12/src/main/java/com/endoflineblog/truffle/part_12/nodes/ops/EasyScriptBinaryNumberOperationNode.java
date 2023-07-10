package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class EasyScriptBinaryNumberOperationNode extends EasyScriptNode {
    public abstract Object executeOperation(VirtualFrame frame, Object arg1, Object arg2);
    public abstract int executeOperationInt(VirtualFrame frame, int arg1, int arg2) throws ArithmeticException;
    public abstract double executeOperationDouble(VirtualFrame frame, double arg1, double arg2);
}
