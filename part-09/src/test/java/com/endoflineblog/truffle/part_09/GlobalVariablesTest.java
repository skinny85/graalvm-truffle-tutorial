package com.endoflineblog.truffle.part_09;

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

public class GlobalVariablesTest {
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
        Value result = this.context.eval("ezs",
                "var a = 1; " +
                "let b = 2 + 3; " +
                "const c = 4 + 5.0; " +
                "(a = a + b + a) + a"
        );

        assertEquals(14, result.asInt());

        Value globalBindings = this.context.getBindings("ezs");
        assertFalse(globalBindings.isNull());
        assertTrue(globalBindings.hasMembers());
        assertTrue(globalBindings.hasMember("a"));
        Value a = globalBindings.getMember("a");
        assertEquals(7, a.asInt());
        assertTrue(globalBindings.hasMember("b"));
        assertTrue(globalBindings.hasMember("c"));
    }

    @Test
    public void variable_declaration_statement_returns_undefined() {
        Value result = this.context.eval("ezs",
                "const $_ = 1;"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void global_variables_are_saved_between_executions() {
        this.context.eval("ezs",
                "var a = 1; " +
                "let b = 2; " +
                "const c = 3.0; "
        );
        Value result = this.context.eval("ezs", "a + b + c;");

        assertEquals(6.0, result.asDouble(), 0.0);
    }

    @Test
    public void variables_without_initializers_have_undefined_value() {
        Value result = this.context.eval("ezs",
                "let a; " +
                "a"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void addition_with_undefined_returns_nan() {
        Value result = this.context.eval("ezs",
                "var a, b = 3; " +
                "a + b"
        );

        assertTrue(result.fitsInDouble());
        assertTrue(Double.isNaN(result.asDouble()));
    }

    @Test
    public void var_declarations_are_hoisted_to_the_top() {
        Value result = this.context.eval("ezs",
                "let b = a; " +
                "var a = 3; " +
                "b"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void hoisted_statement_still_returns_undefined() {
        Value result = this.context.eval("ezs",
                "let a = 1; " +
                "var b = 2"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void reassigning_a_const_causes_an_error() {
        try {
            this.context.eval("ezs",
                    "const a = undefined, b = a; " +
                    "a = b"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Assignment to constant variable 'a'", e.getMessage());
        }
    }

    @Test
    public void const_variables_must_have_an_initializer() {
        try {
            this.context.eval("ezs",
                    "const a;"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Missing initializer in const declaration 'a'", e.getMessage());
        }
    }

    @Test
    public void duplicate_variable_causes_an_error() {
        try {
            this.context.eval("ezs",
                    "var a = 1; "+
                    "let a = 2"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Identifier 'a' has already been declared", e.getMessage());
        }
    }

    @Test
    public void referencing_an_undeclared_variable_causes_an_error() {
        try {
            this.context.eval("ezs", "a");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'a' is not defined", e.getMessage());
        }
    }

    @Test
    public void assigning_to_an_undeclared_variable_causes_an_error() {
        try {
            this.context.eval("ezs", "a = 1");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'a' is not defined", e.getMessage());
        }
    }

    @Test
    public void using_a_variable_in_its_own_definition_causes_an_error() {
        try {
            this.context.eval("ezs", "let x = x");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Cannot access 'x' before initialization", e.getMessage());
        }
    }

    @Test
    public void const_variables_can_be_re_evaluated() {
        String program = "const a = 3; a";
        this.context.eval("ezs", program);
        Value result = this.context.eval("ezs", program);

        assertEquals(3, result.asInt());
    }

    @Test
    public void parsing_a_large_integer_fall_backs_to_double() {
        // this is 12,345,678,901
        Value result = this.context.eval("ezs", "12345678901");

        assertEquals(12_345_678_901D, result.asDouble(), 0.0);
    }
}
