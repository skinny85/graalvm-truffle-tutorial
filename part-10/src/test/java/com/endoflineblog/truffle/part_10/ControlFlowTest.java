package com.endoflineblog.truffle.part_10;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/** This is a set of unit tests for control flow. */
public class ControlFlowTest {
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
                "function f(n) { " +
                "    let ret = n + 2; " +
                "    do { " +
                "        ret = n + 4; " +
                "    } while (false); " +
                "    return ret; " +
                "} " +
                "f(8)"
        );
        assertEquals(12, result.asInt());
    }

    @Test
    public void for_parts_are_all_optional() {
        Value result = this.context.eval("ezs",
                "function fib(n) { " +
                "    if (n < 2) { " +
                "        return n; " +
                "    } " +
                "    let a = 0, b = 1, i = 2; " +
                "    for (;;) { " +
                "        let f = a + b; " +
                "        a = b; " +
                "        b = f; " +
                "        i = i + 1; " +
                "        if (i > n) " +
                "            break; " +
                "        else " +
                "            continue; " +
                "    } " +
                "    return b; " +
                "} " +
                "fib(8)"
        );
        assertEquals(21, result.asInt());
    }

    @Test
    public void for_loop_executes_as_expected() {
        Value result = this.context.eval("ezs",
                "function fib(n) { " +
                "    if (n < 2) { " +
                "        return n; " +
                "    } " +
                "    var a = 0, b = 1; " +
                "    for (let i = 2; i <= n; i = i + 1) { " +
                "        const f = a + b; " +
                "        a = b; " +
                "        b = f; " +
                "    } " +
                "    return b; " +
                "} " +
                "fib(6)"
        );
        assertEquals(8, result.asInt());
    }

    @Test
    public void recursive_fibonacci_works() {
        Value result = this.context.eval("ezs",
                "function fib(n) { " +
                "    if (n > -2) { " +
                "        return Math.abs(n); " +
                "    } " +
                "    return fib(n + 1) + fib(n + 2); " +
                "} " +
                "fib(-9)"
        );
        assertEquals(34, result.asInt());
    }

    @Test
    public void recursive_fibonacci_with_subtraction_works() {
        Value result = this.context.eval("ezs", "" +
                "function fib(n) { " +
                "    if (n < 2) { " +
                "        return n; " +
                "    } " +
                "    return fib(n - 1) + fib(n - 2); " +
                "} " +
                "fib(9)"
        );
        assertEquals(34, result.asInt());
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

    @Test
    public void test_parsing_source() {
        Source fibProgram = Source.create("ezs", "" +
                "function fib(n) { " +
                "    if (n > -2) { " +
                "        return Math.abs(n); " +
                "    } " +
                "    return fib(n + 1) + fib(n + 2); " +
                "} " +
                "fib(-20) " +
                "");
        Value fibProgramValue = this.context.parse(fibProgram);
        assertEquals(6765, fibProgramValue.execute().asInt());
    }

    @Test
    public void functions_are_redefined_on_subsequent_evals() {
        String program = "" +
                "function f() { " +
                "    return 5; " +
                "} " +
                "var sum = 0; " +
                "for (let i = 0; i <= 3; i = i + 1) { " +
                "    if (i === 3) { " +
                "        function f() { return 1; } " +
                "    } " +
                "    sum = sum + f(); " +
                "} " +
                "sum";

        Value firstEvalResult = this.context.eval("ezs", program);
        assertEquals(16, firstEvalResult.asInt());

        Value secondEvalResult = this.context.eval("ezs", program);
        assertEquals(16, secondEvalResult.asInt());
    }
}
