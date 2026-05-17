package com.endoflineblog.truffle.part_16;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClosuresTest {
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
    void nested_non_closure_non_recursive_functions_are_supported() {
        Value result = this.context.eval("ezs", "" +
                "function sum(n) { " +
                "    function sumInternal(n, acc) { " +
                "        while (n > 0) { " +
                "            acc = acc + n; " +
                "            n = n - 1; " +
                "        } " +
                "        return acc; " +
                "    } " +
                "    return sumInternal(n, 0); " +
                "} " +
                "sum(9);"
        );
        assertEquals(45, result.asInt());
    }

    @Test
    void nested_non_closure_recursive_functions_are_supported() {
        Value result = this.context.eval("ezs", "" +
                "function fib(n) { " +
                "    function fibTailRec(m, a, b) { " +
                "        if (m <= 0) " +
                "            return a; " +
                "        return fibTailRec(m - 1, b, a + b); " +
                "    } " +
                "    if (n < 2) " +
                "        return n; " +
                "    return fibTailRec(n, 0, 1); " +
                "} " +
                "fib(10);"
        );
        assertEquals(55, result.asInt());
    }

    @Test
    void nested_closures_can_read_parent_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function fib(n) { " +
                "    const zero = 0; " +
                "    function fibTailRec(m, a, b) { " +
                "        if (m <= zero) " +
                "            return a; " +
                "        return fibTailRec(m - 1, b, a + b); " +
                "    } " +
                "    if (n < 2) " +
                "        return n; " +
                "    return fibTailRec(n, 0, 1); " +
                "} " +
                "fib(11);"
        );
        assertEquals(89, result.asInt());
    }

    @Test
    void nested_closures_can_read_parent_local_variables_two_levels_deep() {
        Value result = this.context.eval("ezs", "" +
                "function fib(n) { " +
                "    const zero = 0; " +
                "    function fibTailRec(m, a, b) { " +
                "        function fibTailRec2(m, a, b) { " +
                "            if (m <= zero) " +
                "                return a; " +
                "            return fibTailRec(m - 1, b, a + b); " +
                "        } " +
                "        return fibTailRec2(m - 1, b, a + b); " +
                "    } " +
                "    if (n < 2) " +
                "        return n; " +
                "    return fibTailRec(n, 0, 1); " +
                "} " +
                "fib(11);"
        );
        assertEquals(89, result.asInt());
    }

    @Test
    void nested_closures_can_write_parent_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function sum(n) { " +
                "    let sum = 0; " +
                "    function sumInternal(n) { " +
                "        for (let i = n; i > 0; i = i - 1) { " +
                "            sum = sum + i; " +
                "        } " +
                "    } " +
                "    sumInternal(n); " +
                "    return sum; " +
                "} " +
                "sum(10);"
        );
        assertEquals(55, result.asInt());
    }

    @Test
    void nested_closures_can_read_parent_function_arguments() {
        Value result = this.context.eval("ezs", "" +
                "function sum(n) { " +
                "    let sum = 0; " +
                "    function sumInternal() { " +
                "        for (let i = n; i > 0; i = i - 1) { " +
                "            sum = sum + i; " +
                "        } " +
                "    } " +
                "    sumInternal(); " +
                "    return sum; " +
                "} " +
                "sum(10);"
        );
        assertEquals(55, result.asInt());
    }

    @Test
    void nested_closures_can_write_parent_function_arguments() {
        Value result = this.context.eval("ezs", "" +
                "function sum(n) { " +
                "    let sum = 0; " +
                "    function sumInternal() { " +
                "        for (; n > 0; n = n - 1) { " +
                "            sum = sum + n; " +
                "        } " +
                "    } " +
                "    sumInternal(); " +
                "    return sum; " +
                "} " +
                "sum(11);"
        );
        assertEquals(66, result.asInt());
    }

    @Test
    void returning_a_closure_is_supported() {
        Value result = this.context.eval("ezs", "" +
                "function makeAdder(add) { " +
                "    function adder(arg) { " +
                "        return arg + add; " +
                "    } " +
                "    return adder; " +
                "} " +
                "const add3 = makeAdder(3); " +
                "add3(4); "
        );
        assertEquals(7, result.asInt());
    }

    @Test
    void anonymous_functions_are_supported() {
        Value result = this.context.eval("ezs", "" +
                "function sum(n) { " +
                "    let sum = 0; " +
                "    ((m) => { " +
                "        for (; m > 0; m = m - 1) { " +
                "            sum = sum + m; " +
                "        } " +
                "    })(n); " +
                "    return sum; " +
                "} " +
                "sum(12);"
        );
        assertEquals(78, result.asInt());
    }
}
