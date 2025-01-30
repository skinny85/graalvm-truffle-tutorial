package com.endoflineblog.truffle.part_16;

import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.DebugScope;
import com.oracle.truffle.api.debug.DebugStackFrame;
import com.oracle.truffle.api.debug.DebugValue;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SuspendAnchor;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.tck.DebuggerTester;
import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebuggerTest {
    private static final String FIB_ITER = "" +
            "class Fib {\n" +
            "    fib(unused, num) {\n" +
            "        var n1 = 0, n2 = 1;\n" +
            "        if (num > 1) {\n" +
            "            let i = 1;\n" +
            "            while (i < num) {\n" +
            "                const next = n1 + n2;\n" +
            "                n1 = n2;\n" +
            "                n2 = next;\n" +
            "                i = i + 1;\n" +
            "            }\n" +
            "            return n2;\n" +
            "        } else {\n" +
            "            return Math.abs(num);\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "const fibM1 = new Fib().fib('unused-1', -1);\n" +
            "let fib2;\n" +
            "fib2 = new Fib().fib('unused2', 2, 'superfluous2');\n" +
            "fibM1 + fib2;\n";

    private DebuggerTester debuggerTester;

    @BeforeEach
    void setUp() {
        this.debuggerTester = new DebuggerTester();
    }

    @AfterEach
    void tearDown() {
        this.debuggerTester.close();
    }

    @Test
    void step_over_global_var_decl_and_into_func_call() {
        Source source = Source.create("ezs", FIB_ITER);

        try (DebuggerSession debuggerSession = this.debuggerTester.startSession()) {
            debuggerSession.suspendNextExecution();
            this.debuggerTester.startEval(source);
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, ":program", 18, SuspendAnchor.BEFORE, "const fibM1 = new Fib().fib('unused-1', -1);");
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, ":program", 19, SuspendAnchor.BEFORE, "let fib2;");
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, ":program", 20, SuspendAnchor.BEFORE, "fib2 = new Fib().fib('unused2', 2, 'superfluous2');");
                event.prepareStepInto(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 3, SuspendAnchor.BEFORE, "var n1 = 0, n2 = 1;",
                        List.of(Map.of("num", "2", "this", "[object Object]", "n1", "undefined", "n2", "undefined")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 3, SuspendAnchor.BEFORE, "var n1 = 0, n2 = 1;",
                        List.of(Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "undefined")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 4, SuspendAnchor.BEFORE, "if (num > 1) {\n" +
                                "            let i = 1;\n" +
                                "            while (i < num) {\n" +
                                "                const next = n1 + n2;\n" +
                                "                n1 = n2;\n" +
                                "                n2 = next;\n" +
                                "                i = i + 1;\n" +
                                "            }\n" +
                                "            return n2;\n" +
                                "        } else {\n" +
                                "            return Math.abs(num);\n" +
                                "        }",
                        List.of(Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "1")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 5, SuspendAnchor.BEFORE, "let i = 1;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "1"),
                                Map.of("i", "undefined")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 6, SuspendAnchor.BEFORE, "while (i < num) {\n" +
                                "                const next = n1 + n2;\n" +
                                "                n1 = n2;\n" +
                                "                n2 = next;\n" +
                                "                i = i + 1;\n" +
                                "            }",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "1"),
                                Map.of("i", "1")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 7, SuspendAnchor.BEFORE, "const next = n1 + n2;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "1"),
                                Map.of("i", "1"),
                                Map.of("next", "undefined")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 8, SuspendAnchor.BEFORE, "n1 = n2;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "1"),
                                Map.of("i", "1"),
                                Map.of("next", "1")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 9, SuspendAnchor.BEFORE, "n2 = next;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "1", "n2", "1"),
                                Map.of("i", "1"),
                                Map.of("next", "1")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 10, SuspendAnchor.BEFORE, "i = i + 1;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "1", "n2", "1"),
                                Map.of("i", "1"),
                                Map.of("next", "1")));
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 12, SuspendAnchor.BEFORE, "return n2;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "1", "n2", "1"),
                                Map.of("i", "2")));
                event.prepareStepOut(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, ":program", 20, SuspendAnchor.AFTER, "fib2 = new Fib().fib('unused2', 2, 'superfluous2');");
                event.prepareStepInto(1);
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, ":program", 21, SuspendAnchor.BEFORE, "fibM1 + fib2;");
                event.prepareStepOver(1);
            });
            this.debuggerTester.expectDone();
        }
    }

    @Test
    void setting_breakpoint_suspends_execution() {
        Source source = Source.create("ezs", FIB_ITER);

        try (DebuggerSession debuggerSession = this.debuggerTester.startSession()) {
            debuggerSession.suspendNextExecution();
            debuggerSession.install(Breakpoint.newBuilder(source.getURI())
                    .lineIs(8)
                    .build());
            this.debuggerTester.startEval(source);
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, ":program", 18, SuspendAnchor.BEFORE, "const fibM1 = new Fib().fib('unused-1', -1);");
                event.prepareContinue();
            });
            this.debuggerTester.expectSuspended(event -> {
                assertState(event, "fib", 8, SuspendAnchor.BEFORE, "n1 = n2;",
                        List.of(
                                Map.of("num", "2", "this", "[object Object]", "n1", "0", "n2", "1"),
                                Map.of("i", "1"),
                                Map.of("next", "1")
                        ));
                event.prepareContinue();
            });

            this.debuggerTester.expectDone();
        }
    }

    private static void assertState(
            SuspendedEvent suspendedEvent, String frameName, int expectedLineNumber,
            SuspendAnchor suspendAnchor, String expectedCode) {
        assertState(suspendedEvent, frameName, expectedLineNumber, suspendAnchor, expectedCode, List.of(Map.of()));
    }

    private static void assertState(
            SuspendedEvent suspendedEvent, String frameName, int expectedLineNumber,
            SuspendAnchor suspendAnchor, String expectedCode, List<Map<String, String>> expectedFrameValues) {
        DebugStackFrame frame = suspendedEvent.getTopStackFrame();
        assertEquals(frameName, frame.getName());

        assertEquals(expectedLineNumber, suspendedEvent.getSourceSection().getStartLine());
        assertEquals(suspendAnchor, suspendedEvent.getSuspendAnchor());
        assertEquals(expectedCode, suspendedEvent.getSourceSection().getCharacters().toString());

        assertEquals(expectedFrameValues, scopeValues(frame.getScope()));
    }

    private static List<Map<String, String>> scopeValues(DebugScope scope) {
        List<Map<String, String>> ret = new LinkedList<>();
        DebugScope currentScope = scope;
        while (currentScope != null) {
            Map<String, String> values = new HashMap<>();
            ret.add(0, values);
            for (DebugValue value : currentScope.getDeclaredValues()) {
                values.put(value.getName(), value.toDisplayString());
            }
            currentScope = currentScope.getParent();
        }
        return ret;
    }
}
