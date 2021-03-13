package com.endoflineblog.truffle.part_04;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PolyglotTest {
    @Test
    public void runs_EasyScript_code() {
        Context context = Context.create();
        Value result = context.eval("ezs",
                "34 + 56.0");
        assertEquals(90.0, result.asDouble(), 0.0);
    }
}
