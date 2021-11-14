package com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;

/**
 * The common ancestor for Nodes that represent the implementations of
 * built-in JavaScript functions.
 * We annotate the class with {@link GenerateNodeFactory},
 * which means the Truffle DSL will generate a {@link com.oracle.truffle.api.dsl.NodeFactory} class for its subclasses.
 * That {@code NodeFactory} is then used in {@link com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage},
 * in its private {@code defineBuiltInFunction()}
 * utility method to add the given built-in function to the global scope.
 */
@NodeChild(value = "arguments", type = EasyScriptExprNode[].class)
@GenerateNodeFactory
public abstract class BuiltInFunctionBodyExprNode extends EasyScriptExprNode {
}
