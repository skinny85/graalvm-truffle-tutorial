package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;

public abstract class EasyScriptBinaryOperationNode extends EasyScriptNode {
    public abstract Object executeOperation(Object lvalue, Object rvalue);
    public abstract int executeOperationInt(int lvalue, int rvalue) throws ArithmeticException;
    public abstract double executeOperationDouble(double lvalue, double rvalue);
}
