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
    void nested_function_can_be_returned_from_outer_one() {
        Value result = this.context.eval("ezs", "" +
                "function makeAdder(n) { " +
                "    function add(arg) { " +
                "        return n + arg; " +
                "    } " +
                "    return add; " +
                "} " +
                "var add3 = makeAdder(3); " +
                "add3(4);"
        );
        assertEquals(7, result.asInt());
    }

    @Test
    void nested_function_can_be_used_multiple_times() {
        Value result = this.context.eval("ezs", "" +
                "function makeAdder(n) { " +
                "    function add(arg) { " +
                "        return n + arg; " +
                "    } " +
                "    return add; " +
                "} " +
                "var add1 = makeAdder('1'); " +
                "var add3 = makeAdder('3'); " +
                "add1(2) + add3(4);"
        );
        assertEquals("1234", result.asString());
    }
}
