package com.endoflineblog.truffle.part_05.nodes.exprs;

import com.endoflineblog.truffle.part_05.EasyScriptTypeSystem;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The abstract common ancestor of all expression Nodes in EasyScript.
 * Very similar to EasyScriptNode from part 3.
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends Node {
    public abstract int executeInt(VirtualFrame frame) throws UnexpectedResultException;

    public abstract double executeDouble(VirtualFrame frame);

    public abstract Object executeGeneric(VirtualFrame frame);
}
