package com.endoflineblog.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExecuteNodesTest {
    @Test
    public void adds_12_and_34_correctly() {
        EasyScriptExprNode exprNode = new AdditionNode(new IntLiteralNode(12), new IntLiteralNode(34));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);

        var result = callTarget.call();

        assertEquals(46, result);
    }
}
