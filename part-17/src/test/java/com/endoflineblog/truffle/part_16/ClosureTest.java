package com.endoflineblog.truffle.part_16;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
                // changing 'm' to 'n' makes the test fail!
                "    function sumInternal(m, acc) { " +
                "        while (m > 0) { " +
                "            acc = acc + m; " +
                "            m = m - 1; " +
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
    @Disabled
    void nested_non_closure_recursive_functions_are_supported() {
        Value result = this.context.eval("ezs", "" +
                "function fib(n) { " +
                "    function fibTailRec(m, a, b) { " +
                "        if (m <= 0) " +
                "            return b; " +
                "        return fibTailRec(m - 1, b, a + b); " +
                "    } " +
                "    if (n < 2) " +
                "        return n; " +
                "    return fibTailRec(n, 0, 1); " +
                "} " +
                "fib(10);"
        );
        assertEquals(7, result.asInt());
    }
}
