package com.endoflineblog.truffle.part_16;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class StaticFunctionCallsTest {
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
    public void calling_Math_abs_works() {
        Value result = this.context.eval("ezs",
                "Math.abs(-2)"
        );

        assertEquals(2, result.asInt());
    }

    @Test
    public void Math_abs_correctly_handles_min_int() {
        Value result = this.context.eval("ezs",
                // if we just use Integer.MIN_VALUE, that will overflow int,
                // as EasyScript parses it as two expressions,
                // negation and an int literal
                "Math.abs(" + (Integer.MIN_VALUE + 1) + " + (-1))"
        );

        assertEquals(Integer.MAX_VALUE + 1D, result.asDouble(), 0.0);
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
    public void adding_a_function_turns_into_string_concatenation() {
        Value result = this.context.eval("ezs",
                "Math.abs + 3"
        );

        assertEquals("[object Function]3", result.asString());
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

    @Test
    public void an_EasyScript_function_can_be_called_from_Java() {
        Value mathAbs = this.context.eval("ezs",
                "Math.abs;"
        );

        assertTrue(mathAbs.canExecute());

        Value result = mathAbs.execute(-3);
        assertEquals(3, result.asInt());
    }

    @Test
    public void calling_an_EasyScript_function_with_a_byte_throws_an_exception() {
        Value mathAbs = this.context.eval("ezs",
                "Math.abs;"
        );

        try {
            byte b = -1;
            mathAbs.execute(b);
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'-1' is not an EasyScript value", e.getMessage());
        }
    }

    @Test
    public void calling_Math_pow_works() {
        Value result = this.context.eval("ezs",
                "Math.pow(2, 3)"
        );

        assertEquals(8, result.asInt());
    }

    @Test
    public void Math_pow_with_negative_exponent_works_correctly() {
        Value result = this.context.eval("ezs",
                "Math.pow(2, -1)"
        );

        assertEquals(0.5, result.asDouble(), 0.0);
    }

    @Test
    public void Math_pow_correctly_switches_to_double_on_overflow() {
        Value result = this.context.eval("ezs",
                "Math.pow(2, 35)"
        );

        assertEquals(34_359_738_368D, result.asDouble(), 0.0);
    }

    @Test
    public void Math_can_be_referenced_by_itself() {
        Value result = this.context.eval("ezs",
                "Math"
        );

        assertTrue(result.hasMembers());
        assertEquals(2, result.getMemberKeys().size()); // 'abs' and 'pow' functions
    }

    @Test
    public void Math_is_not_a_legal_variable_name() {
        try {
            this.context.eval("ezs",
                    "let Math = -5;"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Identifier 'Math' has already been declared", e.getMessage());
        }
    }

    @Test
    public void unknown_function_member_read_through_GraalVM_interop_returns_null() {
        Value abs = this.context.eval("ezs", "Math.abs;");
        assertTrue(abs.hasMembers());
        Value doesNotExist = abs.getMember("doesNotExist");
        assertNull(doesNotExist);
    }
}
