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
}
