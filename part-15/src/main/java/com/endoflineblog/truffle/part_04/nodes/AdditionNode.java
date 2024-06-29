package com.endoflineblog.truffle.part_04.nodes;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The AST node that represents the plus operator in EasyScript.
 * Uses the Truffle DSL.
 * These few lines are all that's required to get equivalent functionality
 * to the hand-written code in {@link com.endoflineblog.truffle.part_02.AdditionNode part 2}.
 * The Truffle DSL annotation processor will generate a class extending this,
 * {@link AdditionNodeGen},
 * that implements the `execute*()` methods.
 */
public final class AdditionNode extends EasyScriptNode {
    @Child
    private EasyScriptNode leftNode;

    @Child
    private EasyScriptNode rightNode;

    public AdditionNode(EasyScriptNode leftNode, EasyScriptNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    @Override
    public int executeInt(VirtualFrame frame) {
        return this.leftNode.executeInt(frame) + this.rightNode.executeInt(frame);
    }
}
