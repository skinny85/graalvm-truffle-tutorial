package com.endoflineblog.truffle.part_07;

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

public class FunctionDefinitionsTest {
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
    public void defining_a_function_works() {
        Value result = this.context.eval("ezs",
                "function f() { Math.pow(4, 3); }" +
                "f()"
        );
        assertEquals(64, result.asInt());
    }

    @Test
    public void cycle_between_var_and_function_works() {
        Value result = this.context.eval("ezs",
                "var v = f();" +
                "function f() {" +
                    "v;" +
                "}" +
                "v"
        );
        assertTrue(result.isNull());
        assertEquals(result.toString(), "undefined");
    }

    @Test
    public void cycle_between_let_and_function_does_not_work() {
        try {
            this.context.eval("ezs",
                    "let v = f(); " +
                    "function f() { " +
                        "v; " +
                    "} " +
                    "v;"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Cannot access 'v' before initialization", e.getMessage());
        }
    }

    @Test
    public void passing_a_parameter_to_a_function_works() {
        Value result = this.context.eval("ezs",
                "function addOne(a) {" +
                    "a + 1; " +
                "} " +
                "addOne(4)"
        );
        assertEquals(5, result.asInt());
    }

    @Test
    public void function_parameters_shadow_each_other() {
        Value result = this.context.eval("ezs",
                "function f(a, a) { " +
                    "a; " +
                "} " +
                "f(1, 23);"
        );
        assertEquals(23, result.asInt());
    }

    @Test
    public void local_variables_shadow_globals() {
        Value result = this.context.eval("ezs",
                "const a = 33; " +
                "function f() { " +
                    "var a = 3; " +
                    "a = 333; " +
                    "a;" +
                "} " +
                "f()"
        );
        assertEquals(333, result.asInt());
    }

    @Test
    public void function_parameters_can_be_reassigned() {
        Value result = this.context.eval("ezs",
                "let a = 222; " +
                "function f(a, b) { " +
                    "b = 22; " +
                    "b; " +
                "} " +
                "f(2);"
        );
        assertEquals(22, result.asInt());
    }

    @Test
    public void functions_can_be_overwritten() {
        Value result = this.context.eval("ezs",
                "function f() { 6; } " +
                "function f() { 7; } " +
                "f(); "
        );
        assertEquals(7, result.asInt());
    }

    @Test
    public void local_variables_get_hoisted() {
        Value result = this.context.eval("ezs",
                "function f() { " +
                    "var a = b; " +
                    "var b = 3; " +
                    "a; " +
                "} " +
                "f();"
        );
        assertTrue(result.isNull());
        assertEquals(result.toString(), "undefined");
    }

    @Test
    public void nested_functions_are_unsupported() {
        try {
            this.context.eval("ezs",
                    "function outer() { " +
                        "function inner() { " +
                        "} " +
                    "}"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("nested functions are not supported in EasyScript yet", e.getMessage());
        }
    }
}
