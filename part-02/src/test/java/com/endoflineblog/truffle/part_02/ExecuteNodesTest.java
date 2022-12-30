package com.endoflineblog.truffle.part_02;

import com.oracle.truffle.api.CallTarget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the manually-written specializations in the {@link AdditionNode} class.
 */
public class ExecuteNodesTest {
    /**
     * A simple test that interprets the expression '12 + 34',
     * and verifies it evaluates to 46.
     */
    @Test
    public void adds_12_and_34_correctly() {
        EasyScriptNode exprNode = new AdditionNode(
                new IntLiteralNode(12),
                new IntLiteralNode(34));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(46, result);
    }

    /**
     * A test that makes sure int overflow turns into double addition.
     */
    @Test
    public void adding_1_to_int_max_does_not_overflow() {
        EasyScriptNode exprNode = new AdditionNode(
                new IntLiteralNode(Integer.MAX_VALUE),
                new IntLiteralNode(1));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(Integer.MAX_VALUE + 1D, result);
    }

    /**
     * A test that shows that ints and doubles can interoperate with each other.
     */
    @Test
    public void adds_2_point_5_and_6_correctly() {
        EasyScriptNode exprNode = new AdditionNode(
                new DoubleLiteralNode(2.5),
                new IntLiteralNode(6));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(8.5, result);
    }
}
