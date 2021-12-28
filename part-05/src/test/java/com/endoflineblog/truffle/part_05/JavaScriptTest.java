package com.endoflineblog.truffle.part_05;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class JavaScriptTest {
    private Context context;

    @Before
    public void setUp() {
        this.context = Context.create();
    }

    @After
    public void tearDown() {
        this.context.close();
    }

    @Test
    public void executing_list_of_statements_returns_the_last_ones_value() {
        Value result = this.context.eval("js",
                "var a = 1; " +
                "let b = 2 + 3; " +
                "const c = 4 + 5.0; " +
                "(a = a + b + a) + a"
        );
        assertEquals(14, result.asInt());

        var globalBindings = context.getBindings("js");
        assertTrue(globalBindings.hasMembers());
        assertTrue(globalBindings.hasMember("a"));
        assertTrue(globalBindings.hasMember("b"));
        assertTrue(globalBindings.hasMember("c"));
        assertEquals(Set.of("a", "b", "c"), globalBindings.getMemberKeys());

        globalBindings.putMember("a", 8);
        Value result2 = this.context.eval("js",
                "a + b + c;"
        );
        assertEquals(22.0, result2.asDouble(), 0.0);
    }

    @Test
    public void variable_declaration_statement_returns_undefined() {
        Value result = this.context.eval("js", "const a = 1;");

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void separate_second_var_definition_is_not_allowed() {
        this.context.eval("js", "let a = 1;");
        try {
            this.context.eval("js", "let a = 2;");
            fail("Expected PolyglotException");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertTrue(e.isSyntaxError());
        }
    }

    @Test
    public void hoisted_statement_still_returns_undefined() {
        Value result = this.context.eval("js",
                "let a = 1; " +
                "var b = 2"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void Math_pow_correctly_switches_to_double_on_overflow() {
        Value result = this.context.eval("js",
                "Math.pow(2, 35)"
        );

        assertEquals(34_359_738_368D, result.asDouble(), 0.0);
    }
}
