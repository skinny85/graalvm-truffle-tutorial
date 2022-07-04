package com.endoflineblog.truffle.part_08.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeChild;

/**
 * The common superclass of all EasyScript expression Nodes that take two arguments.
 * Allows us to save having to put the same {@link NodeChild}
 * annotations on a bunch of class declarations.
 */
@NodeChild("leftSide")
@NodeChild("rightSide")
public abstract class BinaryOperationExprNode extends EasyScriptExprNode {
}
