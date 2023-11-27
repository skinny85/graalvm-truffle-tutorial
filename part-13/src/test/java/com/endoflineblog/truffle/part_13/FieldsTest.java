package com.endoflineblog.truffle.part_13;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldsTest {
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
    void this_in_global_function_is_undefined() {
        Value result = this.context.eval("ezs", "" +
                "function returnThis(ignored) { " +
                "    return this; " +
                "} " +
                "returnThis(3);"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    void returning_this_from_a_method_returns_the_object() {
        Value result = this.context.eval("ezs", "" +
                "class A { " +
                "    returnThis() { " +
                "        return this; " +
                "    } " +
                "} " +
                "let a = new A; " +
                "a === a.returnThis(); "
        );
        assertTrue(result.asBoolean());
    }

    @Test
    void this_is_always_undefined_when_called_through_the_polyglot_api() {
        Value a = this.context.eval("ezs", "" +
                "class A { " +
                "    returnThis() { " +
                "        return this; " +
                "    } " +
                "} " +
                "new A;"
        );
        assertTrue(a.hasMember("returnThis"));
        Value returnThis = a.getMember("returnThis");

        Value result = returnThis.execute();
        assertTrue(result.isNull());
    }

    @Test
    void class_instances_support_write_properties() {
        Value result = this.context.eval("ezs", "" +
                "class A { } " +
                "let a = new A; " +
                "a.p = 15; " +
                "a.p;"
        );
        assertEquals(15, result.asInt());
    }

    @Test
    void instance_properties_override_class_ones() {
        Value result = this.context.eval("ezs", "" +
                "class A { " +
                "    p() { return 'class'; } " +
                "} " +
                "function p() { return 'instance'; } " +
                "let a = new A; " +
                "a.p = p; " +
                "(new A).p() + '_' + a.p();"
        );
        assertEquals("class_instance", result.asString());
    }

    @Test
    void class_instances_have_members_of_prototype_but_not_as_keys() {
        Value obj = this.context.eval("ezs", "" +
                "class obj { " +
                "    a() { } " +
                "} " +
                "let o = new obj; " +
                "o['b'] = 'c'; " +
                "o"
        );
        assertTrue(obj.hasMember("a"));
        assertTrue(obj.hasMember("b"));
        assertEquals(Set.of("b"), obj.getMemberKeys());
    }

    @Test
    public void reading_property_after_write_to_object_works() {
        Value result = this.context.eval("ezs", "" +
                "class Counter { " +
                "    reset() { " +
                "        this.counter = 0; " +
                "    } " +
                "} " +
                "function callReset(counter) { " +
                "    counter.reset(); " +
                "} " +
                "const counter = new Counter(); " +
                "callReset(counter); " +
                "callReset(counter); "
        );
        assertTrue(result.isNull());
    }

    @Test
    public void reading_same_property_after_writing_it_works() {
        Value result = this.context.eval("ezs", "" +
                "class Obj { } " +
                "function readX(input) { " +
                "    return input.x; " +
                "} " +
                "const obj = new Obj(); " +
                "obj.x = 1; " +
                "let x1 = readX(obj); " +
                "obj.x = 3; " +
                "var x2 = readX(obj); " +
                "x1 + x2;"
        );
        assertEquals(4, result.asInt());
    }

    @Test
    public void invoking_functions_inside_arrays_works() {
        Value result = this.context.eval("ezs", "" +
                "function double(n) { return n + n; } " +
                "let arr = [double, double, double]; " +
                "arr[1](2); "
        );
        assertEquals(4, result.asInt());
    }

    @Test
    public void this_gets_populated_in_array_index_reads() {
        Value result = this.context.eval("ezs", "" +
                "function index0OfThis() { " +
                "    return this[0]; " +
                "} " +
                "let arr = [index0OfThis]; " +
                "arr[0]() === index0OfThis; "
        );
        assertTrue(result.asBoolean());
    }

    @Test
    public void benchmark_returns_its_input() {
        var input = 100;
        Value result = this.context.eval("ezs", "" +
                "class Counter { " +
                "    setCount(count) { " +
                "        this.count = count; " +
                "    } " +
                "    getCount() { " +
                "        return this.count; " +
                "    } " +
                "} " +
                "function countWithThisInFor(n) { " +
                "    const counter = new Counter(); " +
                "    for (let i = 1; i <= n; i = i + 1) { " +
                "        counter['setCount'](i); " +
                "    } " +
                "    return counter['getCount'](); " +
                "} " +
                "countWithThisInFor(" + input + ");"
        );
        assertEquals(input, result.asInt());
    }

    @Test
    void global_functions_have_writeable_properties() {
        Value result = this.context.eval("ezs", "" +
                "function f() { } " +
                "f.f = f; " +
                "f.f"
        );
        assertFalse(result.isNull());
    }

    @Test
    void properties_of_Math_can_be_reassigned() {
        Value result = this.context.eval("ezs", "" +
                "function neg(n) { " +
                "    return -n; " +
                "} " +
                "Math.abs = neg; " +
                "Math.abs(3)"
        );
        assertEquals(-3, result.asInt());
    }

    @Test
    void Math_can_be_reassigned() {
        Value result = this.context.eval("ezs", "" +
                "class MyMath { " +
                "    abs(n) { " +
                "        return n; " +
                "    } " +
                "} " +
                "Math = new MyMath(); " +
                "Math.abs(-3)"
        );
        assertEquals(-3, result.asInt());
    }

    @Test
    void arrays_have_directly_writable_properties() {
        Value result = this.context.eval("ezs", "" +
                "let arr = [1, 2, 3]; " +
                "arr.xyz = true; " +
                "arr.xyz"
        );
        assertTrue(result.asBoolean());
    }

    @Test
    public void arrays_have_indexed_writeable_properties() {
        Value result = this.context.eval("ezs", "" +
                "const arr = [0, 1, 2]; " +
                "const result = arr['prop'] = 5; " +
                "[result, arr.prop]"
        );
        assertEquals(5, result.getArrayElement(0).asInt());
        assertEquals(5, result.getArrayElement(1).asInt());
    }

    @Test
    public void non_string_properties_are_converted_to_strings() {
        Value result = this.context.eval("ezs", "" +
                "class A { } " +
                "const a = new A(); " +
                "a[true] = 35; " +
                "a[1 + 2 === 3]"
        );
        assertEquals(35, result.asInt());
    }

    @Test
    public void negative_array_indexes_are_converted_to_strings() {
        Value result = this.context.eval("ezs", "" +
                "let a = [9]; " +
                "a[-1] = 45; " +
                "a[-1]; "
        );
        assertEquals(45, result.asInt());
    }
}
