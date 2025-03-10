package com.endoflineblog.truffle.part_07;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/** This is a set of unit tests for defining functions. */
public class FunctionDefinitionsTest {
    private Context context;

    @BeforeEach
    public void setUp() {
        this.context = Context.create();
    }

    @AfterEach
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
    public void cycle_between_var_and_function_does_not_work() {
        try {
            this.context.eval("ezs",
                "var v = f();" +
                "function f() {" +
                    "v;" +
                "}" +
                "v"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'v' is not defined", e.getMessage());
        }
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
            assertEquals("'v' is not defined", e.getMessage());
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
    public void functions_can_have_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function addOne(a) { " +
                "    let res = a + 1; " +
                "    res; " +
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
    public void function_arguments_shadow_globals() {
        Value result = this.context.eval("ezs", "" +
                "const a = 33; " +
                "function f(a) { " +
                "    a;" +
                "} " +
                "f(22)"
        );
        assertEquals(22, result.asInt());
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
    public void local_variables_shadow_globals_only_from_their_declaration() {
        Value result = this.context.eval("ezs",
                "const b = 5; " +
                "function f() { " +
                    "const a = b; " +
                    "var b = 3; " +
                    "a; " +
                "} " +
                "f();"
        );
        assertEquals(5, result.asInt());
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
        assertEquals(6, result.asInt());
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
    public void cannot_use_a_let_local_variable_before_its_declaration() {
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
            assertEquals("'b' is not defined", e.getMessage());
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

    @Test
    public void functions_can_be_reassigned_as_simple_values() {
        Value result = this.context.eval("ezs", "" +
                "function f() { " +
                "} " +
                "f = 4; " +
                "f"
        );

        assertEquals(4, result.asInt());
    }
}
