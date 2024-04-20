package com.endoflineblog.truffle.part_13.nodes.exprs;

import com.endoflineblog.truffle.part_13.EasyScriptTypeSystem;
import com.endoflineblog.truffle.part_13.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_13.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_13.runtime.Undefined;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The abstract common ancestor of all expression Nodes in EasyScript.
 * Very similar to the class with the same name from part 12,
 * the only differences are the two new methods,
 * {@link #evaluateAsTarget} and {@link #evaluateAsFunction}.
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends EasyScriptNode {
    public abstract Object executeGeneric(VirtualFrame frame);

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
        if (value instanceof TruffleString) {
            return !((TruffleString) value).isEmpty();
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

    /**
     * A more fine-grained alternative to {@link #executeGeneric}
     * that is used for function and method calls.
     * It returns the value that should be used as the receiver of the given function call
     * (the {@code this} variable).
     * Used in {@link com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionCallExprNode},
     * alongside {@link #evaluateAsFunction}.
     * The default implementation returns {@link Undefined#INSTANCE},
     * and is overridden only in {@link com.endoflineblog.truffle.part_13.nodes.exprs.properties.PropertyReadExprNode}
     * and {@link com.endoflineblog.truffle.part_13.nodes.exprs.arrays.ArrayIndexReadExprNode}.
     */
    public Object evaluateAsTarget(VirtualFrame frame) {
        // by default, almost no expressions have a method receiver -
        // the exception is property access
        return Undefined.INSTANCE;
    }

    /**
     * A more fine-grained alternative to {@link #executeGeneric}
     * that is used for function and method calls.
     * Used in {@link com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionCallExprNode},
     * alongside {@link #evaluateAsTarget}.
     * The default implementation is to delegate to {@link #executeGeneric},
     * and is overridden only in {@link com.endoflineblog.truffle.part_13.nodes.exprs.properties.PropertyReadExprNode}
     * and {@link com.endoflineblog.truffle.part_13.nodes.exprs.arrays.ArrayIndexReadExprNode}.
     */
    public Object evaluateAsFunction(VirtualFrame frame, Object target) {
        // by default, the function is simply the result of executing the expression
        return this.executeGeneric(frame);
    }

    public Object evaluateAsThis(VirtualFrame frame, Object target) {
        return target;
    }
}
