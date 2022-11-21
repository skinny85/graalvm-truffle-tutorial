package com.endoflineblog.truffle.part_10;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This is a set of unit tests for testing support for arrays in EasyScript.
 */
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
        Value result = this.context.eval("ezs", "" +
                "const arr = [3, 6]; " +
                "arr[0] + arr[1]"
        );
        assertEquals(9, result.asInt());
    }

    @Test
    public void reading_an_out_of_bound_array_index_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 9][2]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void reading_a_double_array_index_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 9][0.5]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void reading_a_negative_array_index_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 9][-3]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void reading_a_non_number_array_index_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 9][Math.pow]"
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

    @Test
    public void index_in_array_can_be_written_to() {
        Value result = this.context.eval("ezs", "" +
                "let a = [9]; " +
                "a[0] = 45; " +
                "a[0]"
        );
        assertEquals(45, result.asInt());
    }

    @Test
    public void index_beyond_array_size_can_be_assigned_and_fills_array_with_undefined() {
        Value array = this.context.eval("ezs", "" +
                "let a = [9]; " +
                "a[2] = 45; " +
                "a"
        );

        assertEquals(9, array.getArrayElement(0).asInt());

        Value valueAtIndex1 = array.getArrayElement(1);
        assertTrue(valueAtIndex1.isNull());
        assertEquals("undefined", valueAtIndex1.toString());

        assertEquals(45, array.getArrayElement(2).asInt());

        assertEquals(3, array.getArraySize());
    }

    @Test
    public void non_int_indexes_are_ignored_on_write() {
        Value result = this.context.eval("ezs",
                "[1][Math.abs] = 45; "
        );

        assertEquals(45, result.asInt());
    }

    @Test
    public void negative_indexes_are_ignored_on_write() {
        Value array = this.context.eval("ezs", "" +
                "let a = [9]; " +
                "a[-1] = 45; " +
                "a"
        );

        assertEquals(1, array.getArraySize());
        assertEquals(9, array.getArrayElement(0).asInt());
    }
}
