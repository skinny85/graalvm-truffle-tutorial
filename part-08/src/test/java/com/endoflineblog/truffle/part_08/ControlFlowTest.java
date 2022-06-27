package com.endoflineblog.truffle.part_08;

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

/** This is a set of unit tests for control flow. */
public class ControlFlowTest {
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
    public void var_declarations_are_local_in_nested_blocks() {
        Value result = this.context.eval("ezs",
                "var v = 3; " +
                "{ " +
                    "var v = 5; " +
                "} " +
                "v"
        );
        assertEquals(3, result.asInt());
    }

    @Test
    public void var_declarations_are_local_in_nested_blocks_of_functions() {
        Value result = this.context.eval("ezs",
                "function f() { " +
                    "var v = 3; " +
                    "{ " +
                        "var v = 5; " +
                    "} " +
                    "return v; " +
                "} " +
                "f() "
        );
        assertEquals(3, result.asInt());
    }

    @Test
    public void a_function_is_eqal_to_itself_but_not_lte() {
        this.context.eval("ezs",
                "function f() { return false; } " +
                "var t1 = f === f; " +
                "let f1 = f  <  f; " +
                "var f2 = f  <= f; "
        );
        Value bindings = this.context.getBindings("ezs");
        assertTrue(bindings.getMember("t1").asBoolean());
        assertFalse(bindings.getMember("f1").asBoolean());
        assertFalse(bindings.getMember("f2").asBoolean());
    }

    @Test
    public void if_in_a_function_works() {
        this.context.eval("ezs",
                "function sig(n) {" +
                "    if (n < 0) return -1; " +
                "    else if (n > 0) return 1; " +
                "    else return 0; " +
                "} " +
                "var s1 = sig(34); " +
                "var s2 = sig(0); " +
                "var s3 = sig(-12); "
        );
        Value bindings = this.context.getBindings("ezs");
        assertEquals(1, bindings.getMember("s1").asInt());
        assertEquals(0, bindings.getMember("s2").asInt());
        assertEquals(-1, bindings.getMember("s3").asInt());
    }

    @Test
    public void iterative_fibonacci_works() {
        Value result = this.context.eval("ezs",
                "function fib(n) { " +
                "    if (n < 2) { " +
                "        return n; " +
                "    } " +
                "    let a = 0, b = 1, i = 2; " +
                "    while (i <= n) { " +
                "        let f = a + b; " +
                "        a = b; " +
                "        b = f; " +
                "        i = i + 1; " +
                "    } " +
                "    return b; " +
                "} " +
                "fib(7)"
        );
        assertEquals(13, result.asInt());
    }

    @Test
    public void do_while_always_executes_at_least_once() {
        Value result = this.context.eval("ezs",
                "function fib(n) { " +
                "    if (n < 2) { " +
                "        return n; " +
                "    } " +
                "    let a = 0, b = 1, i = 2; " +
                "    do { " +
                "        let f = a + b; " +
                "        a = b; " +
                "        b = f; " +
                "        i = i + 1; " +
                "        if (i > n) " +
                "            break; " +
                "    } while (true); " +
                "    return b; " +
                "} " +
                "fib(7)"
        );
        assertEquals(13, result.asInt());
    }

    @Test
    public void for_loop_executes_as_expected() {
        Value result = this.context.eval("ezs",
                "function fib(n) { " +
                "    if (n < 2) { " +
                "        return n; " +
                "    } " +
                "    let a = 0, b = 1; " +
                "    for (var i = 2; i <= n; i = i + 1) { " +
                "        let f = a + b; " +
                "        a = b; " +
                "        b = f; " +
                "    } " +
                "    return b; " +
                "} " +
                "fib(7)"
        );
        assertEquals(13, result.asInt());
    }

    @Test
    public void if_statement_returns_value() {
        Value result = this.context.eval("ezs",
                "if (true) { " +
                "    42; " +
                "}"
        );
        assertTrue(result.fitsInInt());
        assertEquals(42, result.asInt());
    }

    @Test
    public void return_statement_is_not_allowed_on_top_level() {
        try {
            this.context.eval("ezs",
                    "return;"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("return statement is not allowed outside functions", e.getMessage());
        }
    }
}
