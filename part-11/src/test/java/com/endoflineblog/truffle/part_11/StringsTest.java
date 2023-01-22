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
}
