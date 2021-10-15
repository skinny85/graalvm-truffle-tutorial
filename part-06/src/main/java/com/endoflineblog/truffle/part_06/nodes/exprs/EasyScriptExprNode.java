package com.endoflineblog.truffle.part_06.nodes.exprs;

import com.endoflineblog.truffle.part_06.EasyScriptTypeSystem;
import com.endoflineblog.truffle.part_06.EasyScriptTypeSystemGen;
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
    public abstract Object executeGeneric(VirtualFrame frame);

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }
}
