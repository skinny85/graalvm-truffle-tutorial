package com.endoflineblog.truffle.part_16;

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
    void class_instances_have_members_of_parent_prototypes() {
        Value obj = this.context.eval("ezs", "" +
                "class A { " +
                "    a() { } " +
                "} " +
                "class B extends A { } " +
                "new B; "
        );
        assertTrue(obj.hasMember("a"));
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
    void object_class_can_be_instantiated() {
        Value result = this.context.eval("ezs", "" +
                "let obj = new Object(); " +
                "obj;"
        );

        assertFalse(result.isNull());
    }

    @Test
    void hasOwnProperty_can_be_called_on_class_instance() {
        Value result = this.context.eval("ezs", "" +
                "class Base extends Object { " +
                "    constructor(x) { " +
                "        super(); " +
                "        this.x = x; " +
                "    } " +
                "} " +
                "class Class extends Base { " +
                "    constructor() { " +
                "        super(11); " +
                "    } " +
                "} " +
                "let obj = new Class(); " +
                "obj.hasOwnProperty('x') + '_' + obj.hasOwnProperty('constructor'); "
        );
        assertEquals("true_false", result.asString());
    }

    @Test
    void hasOwnProperty_converts_its_argument_to_string() {
        Value result = this.context.eval("ezs", "" +
                "class Class { " +
                "    constructor() { " +
                "        this['true'] = false; " +
                "    } " +
                "} " +
                "let obj = new Class(); " +
                "obj.hasOwnProperty(true); "
        );
        assertTrue(result.asBoolean());
    }

    @Test
    void hasOwnProperty_sees_only_length_of_strings() {
        Value result = this.context.eval("ezs", "" +
                "'a'.hasOwnProperty('length') + '_' + 'b'.hasOwnProperty('charAt'); "
        );
        assertEquals("true_false", result.asString());
    }

    @Test
    void hasOwnProperty_sees_length_of_arrays() {
        Value result = this.context.eval("ezs",
                "[1, 2].hasOwnProperty('length')"
        );
        assertTrue(result.asBoolean());
    }

    @Test
    void hasOwnProperty_returns_false_for_primitives() {
        Value result = this.context.eval("ezs", "" +
                "true.hasOwnProperty('toString'); "
        );
        assertFalse(result.asBoolean());
    }

    @Test
    void prototypes_can_be_written_to() {
        Value result = this.context.eval("ezs", "" +
                "class Class { " +
                "    m() { " +
                "        return 13; " +
                "    } " +
                "} " +
                "function m() { " +
                "    return 46; " +
                "} " +
                "const obj = new Class(); " +
                "Class.m = m; " +
                "obj.m(); "
        );
        assertEquals(46, result.asInt());
    }

    @Test
    void Object_prototype_can_be_written_to() {
        Value result = this.context.eval("ezs", "" +
                "Object['true'] = 42; " +
                "'string'[true]; "
        );
        assertEquals(42, result.asInt());
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
                "class BaseCounter extends Object { " +
                "    constructor() { " +
                "        super(); " +
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
