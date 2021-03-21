package com.endoflineblog.truffle.part_04;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PolyglotTest {
    private Context context;

    @Before
    public void setUp() {
        this.context = Context.create();
    }

    @After
    public void tearDown() {
        this.context.close();
    }

    /**
     * This test invokes the {@link EasyScriptTruffleLanguage} class
     * through GraalVM's polyglot API.
     */
    @Test
    public void runs_EasyScript_code() {
        Value result = this.context.eval("ezs",
                "10 + 24 + 56.0");
        assertEquals(90.0, result.asDouble(), 0.0);
    }
}
