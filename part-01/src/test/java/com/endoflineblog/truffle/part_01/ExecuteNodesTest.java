package com.endoflineblog.truffle.part_01;

import com.oracle.truffle.api.CallTarget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecuteNodesTest {
    /**
     * A simple test that interprets the expression '12 + 34',
     * and verifies it evaluates to 46.
     */
    @Test
    void adds_12_and_34_correctly() {
        EasyScriptNode exprNode = new AdditionNode(
                new IntLiteralNode(12),
                new IntLiteralNode(34));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        Object result = callTarget.call();

        assertEquals(46, result);
    }
}
