package com.endoflineblog.truffle.part_12;

import com.endoflineblog.truffle.part_12.runtime.Undefined;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompoundAssignmentTest {
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
    void local_variables_prefix_increment_modifies_the_variable() {
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
    void local_variables_prefix_increment_returns_the_new_value() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    return ++local; " +
                "} " +
                "a(); ");

        assertEquals(4, result.asInt());
    }

    @Test
    void local_variables_prefix_decrement_modifies_the_variable() {
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
    void local_variables_prefix_decrement_returns_the_new_value() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    return --local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }

    @Test
    void local_variables_postfix_increment_modifies_the_variable() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    local++; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(4, result.asInt());
    }

    @Test
    void local_variables_postfix_increment_returns_the_old_value() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    return local++; " +
                "} " +
                "a(); ");

        assertEquals(3, result.asInt());
    }

    @Test
    void local_variables_postfix_increment_handles_objects_and_booleans() {
        Value incr = this.context.eval("ezs", "" +
                "function incr(n) { " +
                "    let local = n; " +
                "    local++; " +
                "    return local; " +
                "} " +
                "incr; ");

        assertTrue(Double.isNaN(incr.execute(Undefined.INSTANCE).asDouble()));
        assertEquals(2, incr.execute(1).asInt());
        assertTrue(Double.isNaN(incr.execute(true).asDouble()));
    }

    @Test
    void local_variables_postfix_increment_handles_int_overflow() {
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
    void local_variables_postfix_increment_fails_with_const_variable() {
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
    void local_variables_postfix_decrement_modifies_the_variable() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 3; " +
                "    local--; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }

    @Test
    void local_variables_plus_assignment_modifies_the_variable() {
        Value addTwo = this.context.eval("ezs", "" +
                "function addTwo(n) { " +
                "    let local = 2; " +
                "    local += n; " +
                "    return local; " +
                "} " +
                "addTwo; "
        );

        assertEquals(5, addTwo.execute(3).asInt());
        assertEquals("2ab", addTwo.execute("ab").asString());
        assertTrue(Double.isNaN(addTwo.execute(true).asDouble()));
    }

    @Test
    void local_variables_plus_assignment_returns_the_new_value() {
        Value value = this.context.eval("ezs", "" +
                "function addTwo(n) { " +
                "    let local = 2; " +
                "    return local += n; " +
                "} " +
                "addTwo(3); "
        );

        assertEquals(5, value.asInt());
    }

    @Test
    void global_variables_prefix_increment_modifies_the_variable() {
        Value result = this.context.eval("ezs", "" +
                "let global = 3; " +
                "++global; ");

        assertEquals(4, result.asInt());
        Value global = this.context.getBindings("ezs").getMember("global");
        assertEquals(4, global.asInt());
    }

    @Test
    void global_variables_postfix_increment_modifies_the_variable() {
        Value result = this.context.eval("ezs", "" +
                "let global = 3; " +
                "global++; ");

        assertEquals(3, result.asInt());
        Value global = this.context.getBindings("ezs").getMember("global");
        assertEquals(4, global.asInt());
    }

    @Test
    void global_variables_plus_assignment_modifies_the_variable() {
        Value result = this.context.eval("ezs", "" +
                "let global = 2; " +
                "global += 3; ");

        assertEquals(5, result.asInt());
        Value bindings = this.context.getBindings("ezs");
        assertEquals(5, bindings.getMember("global").asDouble());
    }
}
