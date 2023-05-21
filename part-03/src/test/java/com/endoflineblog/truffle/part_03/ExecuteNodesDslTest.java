package com.endoflineblog.truffle.part_03;

import com.oracle.truffle.api.CallTarget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@link AdditionNode} class that uses TruffleDSL.
 */
public class ExecuteNodesDslTest {
    /**
     * A simple test that interprets the expression '12 + 34',
     * and verifies it evaluates to 46.
     */
    @Test
    public void adds_12_and_34_correctly() {
        // that's how you create instances of `AdditionNodeGen` -
        // using the create() static factory method,
        // instead of a constructor
        EasyScriptNode exprNode = AdditionNodeGen.create(
                new IntLiteralNode(12),
                new IntLiteralNode(34));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(46, result);
    }

    /** A test that shows potential int overflow is handled correctly. */
    @Test
    public void adding_1_to_int_max_does_not_overflow() {
        EasyScriptNode exprNode = AdditionNodeGen.create(
                new IntLiteralNode(Integer.MAX_VALUE),
                new IntLiteralNode(1));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(Integer.MAX_VALUE + 1D, result);
    }

    /** A test that shows how ints and doubles can interoperate. */
    @Test
    public void adds_2_point_5_and_6_correctly() {
        EasyScriptNode exprNode = AdditionNodeGen.create(
                new DoubleLiteralNode(2.5),
                new IntLiteralNode(6));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(8.5, result);
    }
}
