package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;

public abstract class EasyScriptBinaryOpNode extends EasyScriptNode {
    public abstract Object executeOp(Object lvalue, Object rvalue);
    public abstract int executeOpInt(int lvalue, int rvalue) throws ArithmeticException;
    public abstract double executeOpDouble(double lvalue, double rvalue);
}
