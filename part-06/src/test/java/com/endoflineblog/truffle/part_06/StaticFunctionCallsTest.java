package com.endoflineblog.truffle.part_06;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
