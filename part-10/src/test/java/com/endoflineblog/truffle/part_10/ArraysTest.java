package com.endoflineblog.truffle.part_10;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** This is a set of unit tests for testing support for arrays in EasyScript. */
public class ArraysTest {
    private Context context;

    @Before
    public void setUp() {
        this.context = Context.create();
    }

    @After
    public void tearDown() {
        this.context.close();
    }

    @Test
    public void array_literal_is_polyglot_value() {
        Value result = this.context.eval("ezs",
                "[11]"
        );
        assertTrue(result.hasArrayElements());
        assertEquals(1, result.getArraySize());
        assertEquals(11, result.getArrayElement(0).asInt());
    }

    @Test
    public void array_can_be_indexed() {
        Value result = this.context.eval("ezs",
                "[1, 9][1]"
        );
        assertEquals(9, result.asInt());
    }

    @Test
    public void accessing_an_out_of_bound_array_index_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 9][2]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void using_a_double_array_index_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 9][0.5]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void indexing_a_non_array_returns_undefined() {
        Value result = this.context.eval("ezs",
                "3[1]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }
}
