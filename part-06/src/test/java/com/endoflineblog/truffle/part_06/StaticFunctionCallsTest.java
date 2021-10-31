package com.endoflineblog.truffle.part_06;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StaticFunctionCallsTest {
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
    public void calling_Math_abs_works() {
        Value result = this.context.eval("ezs",
                "Math.abs(-2)"
        );

        assertEquals(2, result.asInt());
    }

    @Test
    public void calling_a_function_with_extra_arguments_ignores_the_extra_ones() {
        Value result = this.context.eval("ezs",
                "Math.abs(3, 4);"
        );

        assertEquals(3, result.asInt());
    }

    @Test
    public void extra_function_arguments_expressions_are_still_evaluated() {
        Value result = this.context.eval("ezs",
                "var a = -1; " +
                "Math.abs(4, a = 5);" +
                "a"
        );

        assertEquals(5, result.asInt());
    }

    @Test
    public void calling_a_function_with_less_arguments_assigns_them_undefined() {
        Value result = this.context.eval("ezs",
                "Math.abs()"
        );

        assertTrue(Double.isNaN(result.asDouble()));
    }

    @Test
    public void abs_of_a_function_is_nan() {
        Value result = this.context.eval("ezs",
                "Math.abs(Math.abs)"
        );

        assertTrue(Double.isNaN(result.asDouble()));
    }

    @Test
    public void negating_a_function_or_undefined_returns_NaN() {
        this.context.eval("ezs",
                "var uNeg = -undefined;" +
                "var fNeg = -Math.abs;"
        );

        Value easyScriptBindings = this.context.getBindings("ezs");
        assertTrue(Double.isNaN(easyScriptBindings.getMember("uNeg").asDouble()));
        assertTrue(Double.isNaN(easyScriptBindings.getMember("fNeg").asDouble()));
    }

    @Test
    public void adding_a_function_returns_NaN() {
        Value result = this.context.eval("ezs",
                "Math.abs + 3"
        );

        assertTrue(Double.isNaN(result.asDouble()));
    }

    @Test
    public void calling_a_non_function_throws_a_guest_polyglot_exception() {
        try {
            this.context.eval("ezs",
                    "1(2)"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'1' is not a function", e.getMessage());
        }
    }
}
