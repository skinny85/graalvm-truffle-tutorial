package com.endoflineblog.truffle.part_13.nodes.exprs.functions;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node representing the expression of calling a function in EasyScript,
 * for example {@code Math.pow(2, 3)}.
 * Very similar to the class with the same name from part 12,
 * the main difference is that it's now using the new
 * {@link EasyScriptExprNode#evaluateAsTarget} and
 * {@link EasyScriptExprNode#evaluateAsFunction} methods,
 * and passes the extra 'receiver' argument to
 * {@link FunctionDispatchNode#executeDispatch}.
 */
public final class FunctionCallExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode targetFunction;

    @Children
    private final EasyScriptExprNode[] callArguments;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private FunctionDispatchNode dispatchNode;

    public FunctionCallExprNode(EasyScriptExprNode targetFunction, List<EasyScriptExprNode> callArguments) {
        this.targetFunction = targetFunction;
        this.callArguments = callArguments.toArray(new EasyScriptExprNode[]{});
        this.dispatchNode = FunctionDispatchNodeGen.create();
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object target = this.targetFunction.evaluateAsTarget(frame);
        Object function = this.targetFunction.evaluateAsFunction(frame, target);

        Object[] argumentValues = new Object[this.callArguments.length];
        for (int i = 0; i < this.callArguments.length; i++) {
            argumentValues[i] = this.callArguments[i].executeGeneric(frame);
        }

        Object receiver = this.targetFunction.evaluateAsThis(frame, target);
        return this.dispatchNode.executeDispatch(function, argumentValues, receiver);
    }
}
