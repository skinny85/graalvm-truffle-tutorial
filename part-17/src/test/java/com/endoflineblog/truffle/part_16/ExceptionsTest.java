package com.endoflineblog.truffle.part_16;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A set of unit tests that verify exception handling in EasyScript.
 */
class ExceptionsTest {
    private Context context;
    private String ezsFile;

    @BeforeEach
    void setUp() {
        this.context = Context.create();
    }

    @AfterEach
    void tearDown() {
        this.context.close();
    }

    @Test
    void uncaught_thrown_strings_produce_message_and_stack_trace() throws Exception {
        this.ezsFile = "exceptions-throw-f3.js";

        Source source = Source.newBuilder("ezs", new File("src/test/resources/" + this.ezsFile))
                .build();
        try {
            this.context.eval(source);
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Exception in f3()", e.getMessage());

            StackTraceElement[] stackTrace = e.getStackTrace();
            assertTrue(stackTrace.length >= 5);
            assertStackTraceElIs(stackTrace[0], "f3", 12);
            assertStackTraceElIs(stackTrace[1], "f2", 9);
            assertStackTraceElIs(stackTrace[2], "f1", 5);
            assertStackTraceElIs(stackTrace[3], "main", 2);
            assertStackTraceElIs(stackTrace[4], ":program", 14);
        }
    }

    private void assertStackTraceElIs(StackTraceElement stackTraceEl, String methodName, int lineNr) {
        assertEquals(
                "<ezs>." + methodName + "(" + this.ezsFile + ":" + lineNr + ")",
                stackTraceEl.toString());
    }

    @Test
    void integers_can_be_thrown_and_caught() {
        Value result = this.context.eval("ezs", "" +
                "try { " +
                "    throw 5; " +
                "} catch (e) { " +
                "    e = e + 3; " +
                "    e; " +
                "}"
        );

        assertEquals(8, result.asInt());
    }

    @Test
    void finally_executes_after_throw_and_catch() {
        Value global = this.context.eval("ezs", "" +
                "let global = 0; " +
                "try { " +
                "    throw 5; " +
                "    global = 1; " +
                "} catch (e) { " +
                "    global = 2; " +
                "} finally { " +
                "    global = 3; " +
                "} " +
                "global"
        );

        assertEquals(3, global.asInt());
    }

    @Test
    void finally_preserves_thrown_exception() {
        try {
            this.context.eval("ezs", "" +
                    "function f() { " +
                    "    try { " +
                    "        throw true; " +
                    "    } finally { " +
                    "        3; " +
                    "    } " +
                    "} " +
                    "f();"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("true", e.getMessage());
        }
    }

    @Test
    void finally_without_catch_returns_try_value() {
        Value result = this.context.eval("ezs", "" +
                "try { " +
                "    5; " +
                "} finally { " +
                "    3; " +
                "}"
        );

        assertEquals(5, result.asInt());
    }

    @Test
    void return_in_finally_replaces_thrown_exception() {
        Value result = this.context.eval("ezs", "" +
                "function f() { " +
                "    try { " +
                "        throw 5; " +
                "    } finally { " +
                "        return 3; " +
                "    } " +
                "} " +
                "f();"
        );

        assertEquals(3, result.asInt());
    }

    @Test
    void error_class_has_name_and_message_properties() {
        this.context.eval("ezs", "" +
                "const e = new Error('exception-message'); " +
                "let name = e.name; " +
                "var message = e.message;"
        );
        Value bindings = this.context.getBindings("ezs");
        assertEquals("Error", bindings.getMember("name").asString());
        assertEquals("exception-message", bindings.getMember("message").asString());
    }

    @Test
    void custom_errors_get_name_from_error_superclass() {
        Value result = this.context.eval("ezs", "" +
                "class SecondTypeError extends TypeError { }" +
                "class MyError extends SecondTypeError { }" +
                "const myError = new MyError(); " +
                "myError.name;"
        );

        assertEquals("TypeError", result.asString());
    }

    @Test
    void uncaught_thrown_error_fills_exception_message() {
        try {
            this.context.eval("ezs", "" +
                    "class MyError extends TypeError { } " +
                    "throw new MyError('custom-message');");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("TypeError: custom-message", e.getMessage());
        }
    }

    @Test
    void uncaught_thrown_non_error_object_fills_exception_message() {
        try {
            this.context.eval("ezs", "" +
                    "class NotError {" +
                    "    constructor(name, message) {" +
                    "        this.name = name;" +
                    "        this.message = message;" +
                    "    }" +
                    "} " +
                    "const obj = new NotError('custom-name', 'custom-message'); " +
                    "throw obj;");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("custom-name: custom-message", e.getMessage());
        }
    }

    @Test
    void reading_property_of_undefined_can_be_caught() {
        this.context.eval("ezs", "" +
                "let name = 'name-before';" +
                "var message = 'message-before';" +
                "try {" +
                "    undefined.property;" +
                "} catch (e) {" +
                "    name = e.name;" +
                "    message = e.message;" +
                "}");

        Value bindings = this.context.getBindings("ezs");
        assertEquals("TypeError", bindings.getMember("name").asString());
        assertEquals("Cannot read properties of undefined (reading 'property')", bindings.getMember("message").asString());
    }

    @Test
    void uncaught_read_of_undefined_property_fills_message() {
        try {
            this.context.eval("ezs",
                    "undefined.property;");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("TypeError: Cannot read properties of undefined (reading 'property')", e.getMessage());
        }
    }

    @Test
    void catch_fills_stack_trace() throws IOException {
        this.ezsFile = "exceptions-stack-top-level.js";
        Source source = Source.newBuilder("ezs", new File("src/test/resources/" + this.ezsFile))
                .build();

        this.context.eval(source);

        Value bindings = this.context.getBindings("ezs");
        assertTrue(bindings.getMember("stackBefore").isNull());
        assertEquals("Error: msg\n\tat " + this.ezsFile + ":6:5", bindings.getMember("stackAfter").asString());
    }

    @Test
    void catch_fills_stack_trace_from_func() throws IOException {
        this.ezsFile = "exceptions-stack-from-func.js";
        Source source = Source.newBuilder("ezs", new File("src/test/resources/" + this.ezsFile))
                .build();

        this.context.eval(source);

        Value bindings = this.context.getBindings("ezs");
        assertTrue(bindings.getMember("stackBefore").isNull());
        assertEquals("Error: msg\n\tat f (" + this.ezsFile + ":6:9)\n\tat " + this.ezsFile + ":11:1",
                bindings.getMember("stackAfter").asString());
    }

    @Test
    void benchmark_returns_its_input() {
        int input = 100;
        Value result = this.context.eval("ezs", "" +
                "class Countdown { " +
                "    constructor(start) { " +
                "        this.count = start; " +
                "    } " +
                "    decrement() { " +
                "        if (this.count <= 0) { " +
                "            throw new Error('countdown has completed'); " +
                "        } " +
                "        this.count = this.count - 1; " +
                "    } " +
                "} " +
                "function countdown(n) { " +
                "    const countdown = new Countdown(n); " +
                "    let ret = 0; " +
                "    for (;;) { " +
                "        try { " +
                "            countdown.decrement(); " +
                "            ret = ret + 1; " +
                "        } catch (e) {" +
                "            break; " +
                "        } " +
                "    } " +
                "    return ret; " +
                "} " +
                "countdown(" + input + ");");

        assertEquals(input, result.asInt());
    }
}
