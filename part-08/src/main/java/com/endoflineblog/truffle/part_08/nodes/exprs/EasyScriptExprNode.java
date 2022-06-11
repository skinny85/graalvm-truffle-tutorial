package com.endoflineblog.truffle.part_08.nodes.exprs;

import com.endoflineblog.truffle.part_08.EasyScriptTypeSystem;
import com.endoflineblog.truffle.part_08.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_08.nodes.EasyScriptNode;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The abstract common ancestor of all expression Nodes in EasyScript.
 * Identical to the class with the same name from part 6.
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptExprNode extends EasyScriptNode {
    public abstract Object executeGeneric(VirtualFrame frame);

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return EasyScriptTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }
}
