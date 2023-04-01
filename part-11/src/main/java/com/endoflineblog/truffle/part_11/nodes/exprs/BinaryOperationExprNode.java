package com.endoflineblog.truffle.part_11.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeChild;

/**
 * The common superclass of all EasyScript expression Nodes that take two arguments.
 * Allows us to save having to put the same {@link NodeChild}
 * annotations on a bunch of class declarations.
 * Identical to the class with the same name from part 10.
 */
@NodeChild("leftSide")
@NodeChild("rightSide")
public abstract class BinaryOperationExprNode extends EasyScriptExprNode {
}
