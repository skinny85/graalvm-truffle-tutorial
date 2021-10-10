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
    public void executing_list_of_statements_returns_the_last_ones_value() {
        Value result = this.context.eval("ezs2",
                "var a = 1; " +
                "let b = 2 + 3; " +
                "const c = 4 + 5.0; " +
                "(a = a + b + a) + a"
        );

        assertEquals(14, result.asInt());

        Value globalBindings = this.context.getBindings("ezs2");
        assertFalse(globalBindings.isNull());
        assertTrue(globalBindings.hasMembers());
        assertTrue(globalBindings.hasMember("a"));
        Value a = globalBindings.getMember("a");
        assertEquals(7, a.asInt());
        assertEquals(Set.of("a", "b", "c"), globalBindings.getMemberKeys());
    }
}
