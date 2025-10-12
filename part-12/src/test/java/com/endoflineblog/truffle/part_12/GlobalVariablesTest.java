package com.endoflineblog.truffle.part_12;

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

class GlobalVariablesTest {
    private Context context;

    @BeforeEach
    void setUp() {
        this.context = Context.create();
    }

    @AfterEach
    void tearDown() {
        this.context.close();
    }

    @Test
    void executing_list_of_statements_returns_the_last_ones_value() {
        Value result = this.context.eval("ezs", "" +
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
    void variable_declaration_statement_returns_undefined() {
        Value result = this.context.eval("ezs",
                "const $_ = 1;"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    void global_variables_are_saved_between_executions() {
        this.context.eval("ezs", "" +
                "var a = 1; " +
                "let b = 2; " +
                "const c = 3.0;"
        );
        Value result = this.context.eval("ezs", "a + b + c;");

        assertEquals(6.0, result.asDouble());
    }

    @Test
    void variables_without_initializers_have_undefined_value() {
        Value result = this.context.eval("ezs", "" +
                "let a; " +
                "a"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    void addition_with_undefined_returns_nan() {
        Value result = this.context.eval("ezs", "" +
                "var a, b = 3; " +
                "a + b"
        );

        assertTrue(result.fitsInDouble());
        assertTrue(Double.isNaN(result.asDouble()));
    }

    @Test
    void using_a_variable_before_it_is_defined_causes_an_error() {
        try {
            this.context.eval("ezs", "" +
                    "let b = a; " +
                    "var a = 3; " +
                    "b"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'a' is not defined", e.getMessage());
        }
    }

    @Test
    void reassigning_a_const_causes_an_error() {
        try {
            this.context.eval("ezs", "" +
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
    void const_variables_must_have_an_initializer() {
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
    void duplicate_variable_causes_an_error() {
        try {
            this.context.eval("ezs", "" +
                    "var a = 1; " +
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
    void referencing_an_undeclared_variable_causes_an_error() {
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
    void assigning_to_an_undeclared_variable_causes_an_error() {
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
    void using_a_variable_in_its_own_definition_causes_an_error() {
        try {
            this.context.eval("ezs", "let x = x");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'x' is not defined", e.getMessage());
        }
    }

    @Test
    void const_variables_can_be_re_evaluated() {
        String program = "const a = 3; a";
        this.context.eval("ezs", program);
        Value result = this.context.eval("ezs", program);

        assertEquals(3, result.asInt());
    }

    @Test
    void parsing_a_large_integer_falls_back_to_double() {
        // this is 9,876,543,210
        Value result = this.context.eval("ezs", "9876543210");

        assertEquals(9_876_543_210D, result.asDouble());
    }
}
