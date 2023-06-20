package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectsTest {
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
    public void object_literal_is_polyglot_value() {
        Value result = this.context.eval("ezs",
                "{ a: 3 }"
        );
        assertTrue(result.hasMembers());
        assertTrue(result.hasMember("a"));
    }

    @Test
    public void benchmark_returns_correct_value() {
        Value result = this.context.eval("ezs","" +
                "function countForObject(n) { " +
                "    let ret = 0; " +
                "    for (let i = 0; i < n; i = i + 1) { " +
                "        let obj = {field: i}; " +
                "        ret = ret + obj.field; " +
                "    } " +
                "    return ret; " +
                "} " +
                "countForObject(10000000);"
        );
        assertTrue(result.fitsInDouble());
        assertEquals(49_999_995_000_000D, result.asDouble());
    }
}
