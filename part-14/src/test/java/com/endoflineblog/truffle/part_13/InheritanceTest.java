package com.endoflineblog.truffle.part_13;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class InheritanceTest {
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
    void methods_are_inherited() {
        Value result = this.context.eval("ezs", "" +
                "class A { " +
                "    m() { " +
                "        return 'A'; " +
                "    } " +
                "} " +
                "class B extends A { } " +
                "const bb = new B(); " +
                "bb.m();");

        assertEquals("A", result.toString());
    }

    @Test
    void extending_non_existent_class_is_an_error() {
        try {
            this.context.eval("ezs",
                    "class B extends A { } ");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("class 'B' extends unknown class 'A'", e.getMessage());
        }
    }

    @Test
    public void benchmark_returns_its_input() {
        var input = 100;
        Value result = this.context.eval("ezs", "" +
                "class BaseCounter { " +
                "    constructor() { " +
                "        this.count = 0; " +
                "    } " +
                "    increment() { " +
                "        this.count = this.count + 1; " +
                "    } " +
                "    getCount() { " +
                "        return this.count; " +
                "    } " +
                "} " +
                "class Counter extends BaseCounter { " +
                "} " +
                "function countWithThisInFor(n) { " +
                "    const counter = new Counter(); " +
                "    for (let i = 0; i < n; i = i + 1) { " +
                "        counter['increment'](); " +
                "    } " +
                "    return counter['getCount'](); " +
                "} " +
                "countWithThisInFor(" + input + ");"
        );
        assertEquals(input, result.asInt());
    }
}
