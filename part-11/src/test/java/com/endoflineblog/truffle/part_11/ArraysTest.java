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

    @Test
    public void non_stable_array_reads_work_correctly() {
        Value result = this.context.eval("ezs", "" +
                "function readFirstArrayEl(array) { " +
                "    return array[0]; " +
                "} " +
                "function makeOneElArray() {" +
                "    return [123]; " +
                "} " +
                "readFirstArrayEl(1); " +
                "readFirstArrayEl(2); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(3); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); " +
                "readFirstArrayEl(makeOneElArray()); "
        );

        assertEquals(123, result.asInt());
    }

    @Test
    public void array_properties_can_be_accessed_with_string_indexes() {
        Value result = this.context.eval("ezs", "" +
                "[1, 2, 3]['length']"
        );

        assertEquals(3, result.asInt());
    }

    @Test
    public void an_array_can_be_passed_to_a_function_exec() {
        this.context.eval("ezs", "" +
                "let array = [1, 2, 3]; " +
                "function secondIndex(arr) { " +
                "    return arr[1]; " +
                "}"
        );

        Value globalVariables = this.context.getBindings("ezs");
        Value array = globalVariables.getMember("array");
        Value secondIndex = globalVariables.getMember("secondIndex");
        assertEquals(2, secondIndex.execute(array).asInt());
    }

    @Test
    public void reading_an_index_of_undefined_is_an_error() {
        try {
            this.context.eval("ezs",
                    "undefined[0];"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Cannot read properties of undefined (reading '0')", e.getMessage());
        }
    }

    @Test
    public void writing_an_index_of_undefined_is_an_error() {
        try {
            this.context.eval("ezs",
                    "undefined[0] = 3;"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Cannot set properties of undefined (setting '0')", e.getMessage());
        }
    }
}
