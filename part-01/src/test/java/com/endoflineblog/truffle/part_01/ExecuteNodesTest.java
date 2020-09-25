package com.endoflineblog.truffle.part_01;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExecuteNodesTest {
    /**
     * A simple test that interprets the expression '12 + 34',
     * and verifies it evaluates to 46.
     */
    @Test
    public void adds_12_and_34_correctly() {
        EasyScriptNode exprNode = new AdditionNode(new IntLiteralNode(12), new IntLiteralNode(34));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);

        var result = callTarget.call();

        assertEquals(46, result);
    }
}
