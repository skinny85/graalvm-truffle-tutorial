package com.endoflineblog.truffle.part_06.nodes.exprs;

import com.endoflineblog.truffle.part_06.EasyScriptException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.List;

public final class FunctionCallExprNode extends EasyScriptExprNode {
    @Child
    private EasyScriptExprNode callTarget;

    @Children
    private final EasyScriptExprNode[] callArguments;

    public FunctionCallExprNode(EasyScriptExprNode callTarget, List<EasyScriptExprNode> callArguments) {
        super();
        this.callTarget = callTarget;
        this.callArguments = callArguments.toArray(new EasyScriptExprNode[]{});
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new EasyScriptException("Function call expression not implemented yet");
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new EasyScriptException("Function call expression not implemented yet");
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        throw new EasyScriptException("Function call expression not implemented yet");
    }
}
