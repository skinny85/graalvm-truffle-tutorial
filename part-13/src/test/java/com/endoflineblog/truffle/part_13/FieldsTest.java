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
}