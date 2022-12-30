package com.endoflineblog.truffle.part_05.nodes.exprs;

import com.endoflineblog.truffle.part_05.EasyScriptTypeSystem;
import com.endoflineblog.truffle.part_05.nodes.EasyScriptNode;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The abstract common ancestor of all expression Nodes in EasyScript.
 * Very similar to EasyScriptNode from part 3.
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends EasyScriptNode {
    public abstract int executeInt(VirtualFrame frame) throws UnexpectedResultException;

    /**
     * Since we have the `undefined` value now,
     * we need to allow throwing UnexpectedResultException
     * from {@code executeDouble()} too.
     */
    public abstract double executeDouble(VirtualFrame frame) throws UnexpectedResultException;

    public abstract Object executeGeneric(VirtualFrame frame);
}
