package com.endoflineblog.truffle.part_09.nodes.exprs.functions;

import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node representing the expression of calling a function in EasyScript,
 * for example {@code Math.pow(2, 3)}.
 * Identical to the class with the same name from part 7.
 */
public final class FunctionCallExprNode extends com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode targetFunction;

    @Children
    private final com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode[] callArguments;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private FunctionDispatchNode dispatchNode;

    public FunctionCallExprNode(com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode targetFunction, List<com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode> callArguments) {
        super();
        this.targetFunction = targetFunction;
        this.callArguments = callArguments.toArray(new EasyScriptExprNode[]{});
        this.dispatchNode = FunctionDispatchNodeGen.create();
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object function = this.targetFunction.executeGeneric(frame);

        Object[] argumentValues = new Object[this.callArguments.length];
        for (int i = 0; i < this.callArguments.length; i++) {
            argumentValues[i] = this.callArguments[i].executeGeneric(frame);
        }

        return this.dispatchNode.executeDispatch(function, argumentValues);
    }
}
