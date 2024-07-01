package com.endoflineblog.truffle.part_04.nodes;

import com.endoflineblog.truffle.part_04.EasyScriptTypeSystem;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The base class for all EasyScript Nodes.
 * Practically identical to the {@link com.endoflineblog.truffle.part_02.EasyScriptNode EasyScriptNode class from part 2},
 * except for the {@code @TypeSystemReference} annotation.
 * <p>
 * The TypeSystem is required to correctly make the DSL handle mixing integers and doubles,
 * and the integer overflow cases.
 * Feel free to comment out the @TypeSystemReference line below,
 * and you should see 2 out of 3 tests in ExecuteNodesDslTest fail.
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptNode extends Node {
    public abstract int executeInt(VirtualFrame frame) throws UnexpectedResultException;

    public abstract double executeDouble(VirtualFrame frame);

    public abstract Object executeGeneric(VirtualFrame frame);
}
