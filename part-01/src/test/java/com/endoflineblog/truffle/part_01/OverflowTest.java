package com.endoflineblog.truffle.part_01;

import com.oracle.truffle.api.CallTarget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A test illustrating that the simple addition implementation does not handle overflow correctly.
 * It will be fixed in part 2.
 */
public class OverflowTest {
    @Test
    public void adding_1_to_int_max_overflows() {
        EasyScriptNode exprNode = new AdditionNode(
                new IntLiteralNode(Integer.MAX_VALUE),
                new IntLiteralNode(1));
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(Integer.MIN_VALUE, result);
    }
}
