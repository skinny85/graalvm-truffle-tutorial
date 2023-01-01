package com.endoflineblog.truffle.part_04;

import com.endoflineblog.truffle.part_03.DoubleLiteralNode;
import com.endoflineblog.truffle.part_03.EasyScriptNode;
import com.endoflineblog.truffle.part_03.EasyScriptRootNode;
import com.oracle.truffle.api.CallTarget;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParsingTest {
    @Test
    public void parses_and_executes_easyscript_code_correctly() {
        EasyScriptNode exprNode = EasyScriptTruffleParser.parse("1 + 2 + 3.0 + 4");
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = rootNode.getCallTarget();

        var result = callTarget.call();

        assertEquals(10.0, result);
    }

    @Test(expected = ParseCancellationException.class)
    public void throws_an_exception_when_the_code_cannot_be_parsed() {
        EasyScriptTruffleParser.parse("xyz");
    }

    @Test
    public void parsing_a_large_integer_falls_back_to_double() {
        // this is 9,876,543,210
        String largeInt = "9876543210";
        EasyScriptNode exprNode = EasyScriptTruffleParser.parse(largeInt);

        assertTrue(exprNode instanceof DoubleLiteralNode);
    }
}
