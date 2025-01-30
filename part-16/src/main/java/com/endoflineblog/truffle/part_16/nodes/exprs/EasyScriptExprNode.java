package com.endoflineblog.truffle.part_16.nodes.exprs;

import com.endoflineblog.truffle.part_16.EasyScriptTypeSystem;
import com.endoflineblog.truffle.part_16.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_16.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.endoflineblog.truffle.part_16.nodes.exprs.arrays.ArrayIndexReadExprNode;
import com.endoflineblog.truffle.part_16.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_16.nodes.exprs.properties.PropertyReadExprNode;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The abstract common ancestor of all expression Nodes in EasyScript.
 * Identical to the class with the same name from part 15.
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
     * Used in {@link FunctionCallExprNode},
     * alongside {@link #evaluateAsFunction}.
     * The default implementation returns {@link Undefined#INSTANCE},
     * and is overridden only in {@link PropertyReadExprNode}
     * and {@link ArrayIndexReadExprNode}.
     */
    public Object evaluateAsReceiver(VirtualFrame frame) {
        // by default, almost no expressions have a method receiver -
        // the exception is property access
        return Undefined.INSTANCE;
    }

    /**
     * A more fine-grained alternative to {@link #executeGeneric}
     * that is used for function and method calls.
     * Used in {@link FunctionCallExprNode},
     * alongside {@link #evaluateAsReceiver}.
     * The default implementation is to delegate to {@link #executeGeneric},
     * and is overridden only in {@link PropertyReadExprNode}
     * and {@link ArrayIndexReadExprNode}.
     */
    public Object evaluateAsFunction(VirtualFrame frame, Object receiver) {
        // by default, the function is simply the result of executing the expression
        return this.executeGeneric(frame);
    }
}
