package com.endoflineblog.truffle.part_08.nodes.exprs;

import com.endoflineblog.truffle.part_08.EasyScriptTypeSystem;
import com.endoflineblog.truffle.part_08.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_08.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The abstract common ancestor of all expression Nodes in EasyScript.
 * Almost identical to the class with the same name from part 7,
 * the only difference is the additional {@link #executeBool}
 * method from {@link EasyScriptExprNode}.
 *
 * @see #executeBool
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends EasyScriptNode {
    public abstract Object executeGeneric(VirtualFrame frame);

    /**
     * Evaluate a given expression as a boolean.
     * Unlike the other {@code execute*()} methods,
     * doesn't throw {@link UnexpectedResultException},
     * as every value in JavaScript can be interpreted as a boolean -
     * only {@code false}, {@code 0} and {@code undefined} are interpreted as {@code false},
     * the remaining values are {@code true}.
     */
    public boolean executeBool(VirtualFrame frame) {
        Object value = this.executeGeneric(frame);
        // 'undefined' is falsy
        if (value == Undefined.INSTANCE) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        // a number is falsy when it's 0
        if (value instanceof Integer) {
            return (Integer) value != 0;
        }
        if (value instanceof Double) {
            return (Double) value != 0.0;
        }
        // all other values are truthy
        return true;
    }

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }
}
