package com.endoflineblog.truffle.part_11;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This is a set of unit tests for testing support for (read-only)
 * properties in EasyScript.
 */
public class PropertiesTest {
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
    public void array_has_length_property() {
        Value result = this.context.eval("ezs",
                "[1, 2, 3].length"
        );
        assertEquals(3, result.asInt());
    }

    @Test
    public void bubble_sort_changes_array_to_sorted() {
        Value result = this.context.eval("ezs", "" +
                "const array = [4, 3, 2, 1]; " +
                "function bubbleSort(array) { " +
                "    for (var i = 0; i < array.length - 1; i = i + 1) { " +
                "        for (var j = 0; j < array.length - 1 - i; j = j + 1) { " +
                "            if (array[j] > array[j + 1]) { " +
                "                var tmp = array[j]; " +
                "                array[j] = array[j + 1]; " +
                "                array[j + 1] = tmp; " +
                "            } " +
                "        } " +
                "    } " +
                "} " +
                "bubbleSort(array); " +
                "array"
        );

        assertEquals(1, result.getArrayElement(0).asInt());
        assertEquals(2, result.getArrayElement(1).asInt());
        assertEquals(3, result.getArrayElement(2).asInt());
        assertEquals(4, result.getArrayElement(3).asInt());
    }

    @Test
    public void reading_a_property_of_undefined_is_an_error() {
        try {
            this.context.eval("ezs",
                    "undefined.abc;"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Cannot read properties of undefined (reading 'abc')", e.getMessage());
        }
    }

    @Test
    public void non_existent_array_property_returns_undefined() {
        Value result = this.context.eval("ezs",
                "[1, 2, 3].abc"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void any_property_of_integer_returns_undefined() {
        Value result = this.context.eval("ezs",
                "1.toString"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }
}
