package com.endoflineblog.truffle.part_04;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolyglotTest {
    private Context context;
    private ByteArrayOutputStream logHandler;

    @BeforeEach
    public void setUp() {
        this.logHandler = new ByteArrayOutputStream();
        this.context = Context.newBuilder()
                .option("engine.TraceCompilation", "true")
                .logHandler(this.logHandler)
                .allowExperimentalOptions(true)
                .option("engine.CompileAOTOnCreate", "true")
                .build();
    }

    @AfterEach
    public void tearDown() {
        this.context.close();
    }

    @Test
    public void addition_of_three_integers_with_aot() {
        Value result = this.context.eval("ezs",
                "10 + 24 + 56");
        assertEquals(90, result.asInt());

        String log = this.logHandler.toString();
        assertTrue(log.contains("[engine] opt done"), "Expected engine log (" + log + ") to contain '[engine] opt done'");
        assertTrue(log.contains("EasyScriptRootNode"), "Expected engine log (" + log + ") to contain 'EasyScriptRootNode'");
    }
}
