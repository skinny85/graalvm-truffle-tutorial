package com.endoflineblog.truffle.part_04;

import com.endoflineblog.truffle.part_03.DoubleLiteralNode;
import com.endoflineblog.truffle.part_03.EasyScriptNode;
import com.endoflineblog.truffle.part_03.EasyScriptRootNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParsingTest {
    @Test
    public void parses_and_executes_easyscript_code_correctly() {
        EasyScriptNode exprNode = EasyScriptTruffleParser.parse("1 + 2 + 3.0 + 4");
        var rootNode = new EasyScriptRootNode(exprNode);
        CallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);

        var result = callTarget.call();

        assertEquals(10.0, result);
    }

    @Test(expected = ParseCancellationException.class)
    public void throws_an_exception_when_the_code_cannot_be_parsed() {
        EasyScriptTruffleParser.parse("xyz");
    }

    @Test
    public void parsing_a_large_integer_fall_backs_to_double() {
        // this is 12,345,678,901
        String largeInt = "12345678901";
        EasyScriptNode exprNode = EasyScriptTruffleParser.parse(largeInt);

        assertTrue(exprNode instanceof DoubleLiteralNode);
    }
}
