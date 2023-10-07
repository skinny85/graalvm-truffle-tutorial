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
                "function returnThis() { " +
                "    return this; " +
                "} " +
                "returnThis();"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }
}
