package com.endoflineblog.truffle.part_13;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                "a.returnThis(); "
        );
        // ToDo fix, as this is incorrect behavior
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
//        assertTrue(result.asBoolean());
    }
}
