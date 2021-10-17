package com.endoflineblog.truffle.part_06;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StaticFunctionCallsTest {
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
    public void calling_Math_abs_works() {
        Value result = this.context.eval("ezs",
                "Math.abs(2)"
        );

        assertEquals(2, result.asInt());
    }
}
