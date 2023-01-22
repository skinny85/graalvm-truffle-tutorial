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
                "const blankStr = ' '; " +
                "if (blankStr) { " +
                "    ret = 'blank string is truthy'; " +
                "} else { " +
                "    ret = 'blank string is falsy'; " +
                "} " +
                "ret"
        );
        assertTrue(result.isString());
        assertEquals("blank string is truthy", result.asString());
    }
}
