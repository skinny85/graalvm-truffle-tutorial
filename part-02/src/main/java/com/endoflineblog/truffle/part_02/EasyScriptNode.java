package com.endoflineblog.truffle.part_02;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The base class for all EasyScript Nodes.
 * Contains multiple 'execute' methods to implement specializations.
 *
 * @see #executeInt
 * @see #executeDouble
 * @see #executeGeneric
 */
public abstract class EasyScriptNode extends Node {
    /**
     * This 'execute' method throws the {@link UnexpectedResultException}
     * when the value of a given Node cannot be represented as an `int` -
     * for example, in {@link DoubleLiteralNode}.
     */
    public abstract int executeInt(VirtualFrame frame) throws UnexpectedResultException;

    public abstract double executeDouble(VirtualFrame frame);

    /**
     * This method is not strictly required in this part,
     * as {@link #executeDouble} handles all possible values of the language at this point
     * (as we only implement number addition),
     * but we'll need it later anyway,
     * and Truffle DSL (used in part 3) requires it,
     * so we might as well add it now.
     */
    public abstract Object executeGeneric(VirtualFrame frame);
}
