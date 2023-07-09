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

public class InDeCrementTest {
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
    void prefix_increment_works_for_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    ++local; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(4, result.asInt());
    }

    @Test
    void prefix_increment_returns_the_new_value() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    return ++local; " +
                "} " +
                "a(); ");

        assertEquals(4, result.asInt());
    }

    @Test
    void prefix_decrement_works_for_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    --local; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }

    @Test
    void prefix_decrement_returns_the_new_value() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    return --local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }

    @Test
    void postfix_increment_works_for_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 1; " +
                "    local++; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }

    @Test
    void postfix_increment_returns_the_old_value() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    return local++; " +
                "} " +
                "a(); ");

        assertEquals(3, result.asInt());
    }

    @Test
    void postfix_increment_works_for_local_variables_that_were_objects() {
        Value incr = this.context.eval("ezs", "" +
                "function incr(n) { " +
                "    let local = n; " +
                "    local++; " +
                "    return local; " +
                "} " +
                "incr; ");

        assertTrue(Double.isNaN(incr.execute().asDouble()));
        assertEquals(2, incr.execute(1).asInt());
    }

    @Test
    void postfix_increment_works_for_int_overflow() {
        Value result = this.context.eval("ezs", "" +
                "function overflow() { " +
                "    let local = " + Integer.MAX_VALUE + "; " +
                "    local++; " +
                "    return local; " +
                "} " +
                "overflow(); ");

        assertEquals(Integer.MAX_VALUE + 1D, result.asDouble());
    }

    @Test
    void postfix_increment_fails_for_const_local_variable() {
        try {
            this.context.eval("ezs", "" +
                    "function const_incr(n) { " +
                    "    const local = n; " +
                    "    local++; " +
                    "    return local; " +
                    "} " +
                    "const_incr(3); ");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Assignment to constant variable 'local'", e.getMessage());
        }
    }

    @Test
    void postfix_decrement_works_for_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    local--; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }
}
