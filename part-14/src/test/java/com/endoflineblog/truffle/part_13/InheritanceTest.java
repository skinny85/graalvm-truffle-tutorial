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
    void super_reads_property_of_parent_prototype() {
        Value result = this.context.eval("ezs", "" +
                "class Base { " +
                "    m() { " +
                "        return 'Base'; " +
                "    } " +
                "} " +
                "class Derived extends Base { " +
                "    m() { " +
                "        return 'Derived'; " +
                "    } " +
                "    callSuperM() { " +
                "        return super.m(); " +
                "    } " +
                "    callThisM() { " +
                "        return this.m(); " +
                "    } " +
                "} " +
                "const obj = new Derived(); " +
                "obj.callSuperM() + '_' + obj.callThisM();"
        );

        assertEquals("Base_Derived", result.asString());
    }

    @Test
    void super_is_static_not_dynamic() {
        Value result = this.context.eval("ezs", "" +
                "class Base { " +
                "    m() { " +
                "        return 'Base'; " +
                "    } " +
                "} " +
                "class Middle extends Base { " +
                "    m() { " +
                "        return 'Middle'; " +
                "    } " +
                "    callSuperM() { " +
                "        return super.m(); " +
                "    } " +
                "} " +
                "class Derived extends Middle { " +
                "    m() {" +
                "        return 'Derived'; " +
                "    } " +
                "} " +
                "const obj = new Derived(); " +
                "obj.callSuperM();"
        );

        assertEquals("Base", result.asString());
    }

    @Test
    void writing_to_super_writes_to_this() {
        Value result = this.context.eval("ezs", "" +
                "class Base { " +
                "    constructor() { " +
                "        this.x = 11; " +
                "    } " +
                "} " +
                "class Derived extends Base { " +
                "    setSuperX(x) {" +
                "        super.x = x; " +
                "    } " +
                "} " +
                "const obj = new Derived(); " +
                "let x = obj.x; " +
                "obj.setSuperX(3); " +
                "x + obj.x;"
        );

        assertEquals(14, result.asInt());
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
                "class LowerMiddleCounter extends BaseCounter { " +
                "} " +
                "class UpperMiddleCounter extends LowerMiddleCounter { " +
                "    constructor() { " +
                "        super(); " +
                "    } " +
                "    increment() { " +
                "        return super.increment(); " +
                "    } " +
                "    getCount() { " +
                "        return super['getCount'](); " +
                "    } " +
                "} " +
                "class Counter extends UpperMiddleCounter { " +
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
