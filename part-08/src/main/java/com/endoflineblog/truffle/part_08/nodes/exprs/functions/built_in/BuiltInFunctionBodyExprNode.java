package com.endoflineblog.truffle.part_08.nodes.exprs.functions.built_in;

import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;

/**
 * The common ancestor for Nodes that represent the implementations of
 * built-in JavaScript functions.
 * Identical to the class with the same name from part 7.
 */
@NodeChild(value = "arguments", type = ReadFunctionArgExprNode[].class)
@GenerateNodeFactory
public abstract class BuiltInFunctionBodyExprNode extends EasyScriptExprNode {
}
