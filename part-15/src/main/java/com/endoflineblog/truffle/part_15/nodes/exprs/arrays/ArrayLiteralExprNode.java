package com.endoflineblog.truffle.part_15.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_15.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_15.runtime.ArrayObject;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node representing array literal expressions
 * (like {@code [1, 2, 3]}).
 * Identical to the class with the same name from part 14.
 */
public final class ArrayLiteralExprNode extends EasyScriptExprNode {
    @Children
    private final EasyScriptExprNode[] arrayElementExprs;

    public ArrayLiteralExprNode(List<EasyScriptExprNode> arrayElementExprs) {
        this.arrayElementExprs = arrayElementExprs.toArray(new EasyScriptExprNode[]{});
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object[] arrayElements = new Object[this.arrayElementExprs.length];
        for (var i = 0; i < this.arrayElementExprs.length; i++) {
            arrayElements[i] = this.arrayElementExprs[i].executeGeneric(frame);
        }
        ShapesAndPrototypes shapesAndPrototypes = this.currentLanguageContext().shapesAndPrototypes;
        return new ArrayObject(shapesAndPrototypes.arrayShape,
                shapesAndPrototypes.arrayPrototype, arrayElements);
    }
}
