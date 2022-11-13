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
    public void array_literal_can_be_used() {
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
}
