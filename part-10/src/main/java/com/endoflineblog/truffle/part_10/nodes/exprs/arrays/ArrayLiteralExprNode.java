package com.endoflineblog.truffle.part_10.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.runtime.ArrayObject;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.object.Shape;

import java.util.List;

/**
 * The Node representing array literal expressions
 * (like {@code [1, 2, 3]}).
 */
public final class ArrayLiteralExprNode extends EasyScriptExprNode {
    private final Shape arrayShape;
    @Children
    private final EasyScriptExprNode[] arrayElementExprs;

    public ArrayLiteralExprNode(Shape arrayShape, List<EasyScriptExprNode> arrayElementExprs) {
        this.arrayShape = arrayShape;
        this.arrayElementExprs = arrayElementExprs.toArray(new EasyScriptExprNode[]{});
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object[] arrayElements = new Object[this.arrayElementExprs.length];
        for (var i = 0; i < this.arrayElementExprs.length; i++) {
            arrayElements[i] = this.arrayElementExprs[i].executeGeneric(frame);
        }
        return new ArrayObject(this.arrayShape, arrayElements);
    }
}
