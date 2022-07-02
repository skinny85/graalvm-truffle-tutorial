package com.endoflineblog.truffle.part_08.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild("leftSide")
@NodeChild("rightSide")
public abstract class BinaryOperationExprNode extends EasyScriptExprNode {
}
