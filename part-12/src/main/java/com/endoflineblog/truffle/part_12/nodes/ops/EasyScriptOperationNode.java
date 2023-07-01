package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A common superclass of all operation Nodes in EasyScript.
 * An operation is similar to an expression,
 * but instead of having child sub-expressions,
 * the way something like addition does,
 * operations instead produce a value by performing computations directly
 * on the value they receive as an argument,
 * but don't have any children themselves.
 * Currently, we have two operations in EasyScript:
 * increment (adding 1 to a numeric value) and
 * decrement (subtracting 1 from a numeric value).
 */
public abstract class EasyScriptOperationNode extends EasyScriptNode {
    public abstract Object executeOperation(VirtualFrame frame, Object value);
    public abstract int executeOperationInt(VirtualFrame frame, int value);
    public abstract double executeOperationDouble(VirtualFrame frame, double value);
}
