package com.endoflineblog.truffle.part_05;

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

public class ExecutingTest {
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
    }

    @Test
    public void reassigning_a_const_causes_an_error() {
        try {
            this.context.eval("ezs",
                    "const a = 1, b = a; "+
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
}
