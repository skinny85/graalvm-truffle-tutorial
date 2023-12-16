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
    void methods_can_be_called_through_polyglot_api() {
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
        assertFalse(result.isNull());
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
                "        counter.count = i; " +
//                "        counter.setCount(i); " +
                "    } " +
                "    return counter.count; " +
//                "    return counter.getCount(); " +
                "} " +
                "countWithThisInFor(" + input + ");"
        );
        assertEquals(input, result.asInt());
    }

    @Test
    public void simplelang_benchmark_returns_its_input() {
        var input = 100;
        this.context.eval("sl", "" +
                "function countWithThisInFor(n) { " +
                "    counter = new(); " +
                "    i = 1; " +
                "    while (i <= n) { " +
                "        counter.count = i; " +
                "        i = i + 1; " +
                "    } " +
                "    return counter.count; " +
                "}");

        Value result = this.context.eval("sl", "" +
                "function main() { " +
                "    return countWithThisInFor(" + input + ");" +
                "}"
        );
        assertEquals(input, result.asInt());
    }
}
