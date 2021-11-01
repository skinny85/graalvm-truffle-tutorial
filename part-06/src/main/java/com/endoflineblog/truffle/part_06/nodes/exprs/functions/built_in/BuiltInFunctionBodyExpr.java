package com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild(value = "arguments", type = EasyScriptExprNode[].class)
@GenerateNodeFactory
public abstract class BuiltInFunctionBodyExpr extends EasyScriptExprNode {
}
