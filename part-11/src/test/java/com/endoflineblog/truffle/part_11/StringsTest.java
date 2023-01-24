package com.endoflineblog.truffle.part_11;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This is a set of unit tests for testing strings in EasyScript.
 */
public class StringsTest {
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
    public void strings_can_be_created_with_single_quotes() {
        Value result = this.context.eval("ezs",
                "''"
        );
        assertTrue(result.isString());
        assertEquals("", result.asString());
    }

    @Test
    public void single_quote_strings_can_contain_a_single_quote_by_escaping_it() {
        Value result = this.context.eval("ezs",
                "'\\''"
        );
        assertTrue(result.isString());
        assertEquals("'", result.asString());
    }

    @Test
    public void empty_string_is_falsy() {
        Value result = this.context.eval("ezs", "" +
                "let ret; " +
                "if ('') { " +
                "    ret = 'empty string is truthy'; " +
                "} else { " +
                "    ret = 'empty string is falsy'; " +
                "} " +
                "ret"
        );
        assertTrue(result.isString());
        assertEquals("empty string is falsy", result.asString());
    }

    @Test
    public void blank_string_is_truthy() {
        Value result = this.context.eval("ezs", "" +
                "let ret; " +
                "if (' ') { " +
                "    ret = 'blank string is truthy'; " +
                "} else { " +
                "    ret = 'blank string is falsy'; " +
                "} " +
                "ret"
        );
        assertTrue(result.isString());
        assertEquals("blank string is truthy", result.asString());
    }

    @Test
    public void strings_can_be_concatenated() {
        Value result = this.context.eval("ezs",
                "'abc' + '_' + 'def'"
        );
        assertTrue(result.isString());
        assertEquals("abc_def", result.asString());
    }

    @Test
    public void properties_can_be_accessed_through_indexing() {
        Value result = this.context.eval("ezs", "" +
                "const arr = [0, 1, 2]; " +
                "arr['length']"
        );
        assertEquals(3, result.asInt());
    }

    @Test
    public void property_writes_through_indexing_are_ignored() {
        Value result = this.context.eval("ezs", "" +
                "const arr = [0, 1, 2]; " +
                "const result = arr['length'] = 5;" +
                "[result, arr.length]"
        );
        assertEquals(5, result.getArrayElement(0).asInt());
        assertEquals(3, result.getArrayElement(1).asInt());
    }

    @Test
    public void strings_have_a_length_property() {
        Value result = this.context.eval("ezs",
                "'length'.length"
        );
        assertEquals(6, result.asInt());
    }

    @Test
    public void java_strings_can_be_used_for_indexing() {
        this.context.eval("ezs", "" +
                "function access(o, propertyName) { " +
                "    return o[propertyName]; " +
                "} " +
                "let str = 'ab'; "
        );
        Value ezsBindings = this.context.getBindings("ezs");
        Value str = ezsBindings.getMember("str");
        Value access = ezsBindings.getMember("access");

        assertEquals(2, access.execute(str, "length").asInt());
    }
}
