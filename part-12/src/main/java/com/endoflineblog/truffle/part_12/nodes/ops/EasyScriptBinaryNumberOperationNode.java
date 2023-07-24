package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;

public abstract class EasyScriptBinaryNumberOperationNode extends EasyScriptNode {
    public abstract Object executeOperation(Object arg1, Object arg2);
    public abstract int executeOperationInt(int arg1, int arg2) throws ArithmeticException;
    public abstract double executeOperationDouble(double arg1, double arg2);
}
