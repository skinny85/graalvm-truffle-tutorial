package com.endoflineblog.truffle.part_10;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
