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
                    "} "
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
    public void functions_can_be_redefined() {
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
                "const b = 5; " +
                "function f() { " +
                    "const a = b; " +
                    "var b = 3; " +
                    "a; " +
                "} " +
                "f();"
        );
        assertTrue(result.isNull());
        assertEquals(result.toString(), "undefined");
    }

    @Test
    public void higher_order_functions_are_supported() {
        Value result = this.context.eval("ezs",
                "function f() { 5; } " +
                "function g(a) { " +
                    "1 + a(); " +
                "} " +
                "g(f);"
        );
        assertEquals(result.asInt(), 6);
    }

    @Test
    public void const_local_variables_cannot_be_reassigned() {
        try {
            this.context.eval("ezs",
                    "function f() { " +
                        "const a = 5; " +
                        "a = 10; " +
                    "}"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Assignment to constant variable 'a'", e.getMessage());
        }
    }

    @Test
    public void cannot_use_a_let_variable_before_initialization() {
        try {
            this.context.eval("ezs",
                    "function f() { " +
                        "const a = b; " +
                        "let b = 10; " +
                    "} " +
                    "f()"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Cannot access 'b' before initialization", e.getMessage());
        }
    }

    @Test
    public void var_cannot_override_a_function() {
        try {
            this.context.eval("ezs",
                    "var f = 5; " +
                    "function f() { " +
                        "6; " +
                    "}"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Identifier 'f' has already been declared", e.getMessage());
        }
    }

    @Test
    public void duplicate_vars_in_a_function_cause_an_error() {
        try {
            this.context.eval("ezs",
                    "function f() { " +
                        "var a = 1; " +
                        "var a = 2; " +
                    "}"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Identifier 'a' has already been declared", e.getMessage());
        }
    }

    @Test
    public void var_shadowing_a_function_argument_is_not_allowed() {
        try {
            this.context.eval("ezs",
                    "function f(a) { " +
                        "var a = 1; " +
                    "}"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Identifier 'a' has already been declared", e.getMessage());
        }
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
