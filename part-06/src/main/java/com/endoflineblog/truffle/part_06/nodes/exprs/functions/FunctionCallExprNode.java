package com.endoflineblog.truffle.part_06.nodes.exprs.functions;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node representing the expression of calling a function in EasyScript,
 * for example {@code Math.pow(2, 3)}.
 */
public final class FunctionCallExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode callTarget;

    @Children
    private final EasyScriptExprNode[] callArguments;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private FunctionDispatchNode dispatchNode;

    public FunctionCallExprNode(EasyScriptExprNode callTarget, List<EasyScriptExprNode> callArguments) {
        super();
        this.callTarget = callTarget;
        this.callArguments = callArguments.toArray(new EasyScriptExprNode[]{});
        this.dispatchNode = FunctionDispatchNodeGen.create();
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object function = this.callTarget.executeGeneric(frame);

        Object[] argumentValues = new Object[this.callArguments.length];
        for (int i = 0; i < this.callArguments.length; i++) {
            argumentValues[i] = this.callArguments[i].executeGeneric(frame);
        }

        return this.dispatchNode.executeDispatch(function, argumentValues);
    }
}
