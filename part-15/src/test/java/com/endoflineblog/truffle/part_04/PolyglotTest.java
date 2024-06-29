package com.endoflineblog.truffle.part_04;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        Value program = this.context.parse("ezs",
                "10 + 24 + 56");

        String afterParseLog = this.logHandler.toString();
        this.logHandler.reset();
        assertTrue(afterParseLog.contains("[engine] opt done"), "Expected engine log after parsing (" + afterParseLog + ") to contain '[engine] opt done'");
        assertTrue(afterParseLog.contains("EasyScriptRootNode"), "Expected engine log after parsing (" + afterParseLog + ") to contain 'EasyScriptRootNode'");

        Value result = program.execute();
        assertEquals(90, result.asInt());

        String afterExecuteLog = this.logHandler.toString();
        assertFalse(afterExecuteLog.contains("inval"), "Expected engine log after execution (" + afterExecuteLog + ") not to contain 'inval'");
    }
}
