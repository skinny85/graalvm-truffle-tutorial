package com.endoflineblog.truffle.part_13;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This is a set of unit tests for validating support for user-defined classes in EasyScript.
 */
public class ClassesTest {
    private Context context;

    @BeforeEach
    void setUp() {
        this.context = Context.create();
    }

    @AfterEach
    void tearDown() {
        this.context.close();
    }

    @Test
    void class_declaration_creates_object() {
        Value result = this.context.eval("ezs", "" +
                "class A { } " +
                "A;");

        assertEquals("[class A]", result.toString());
        assertFalse(result.hasMembers());
    }

    @Test
    void class_can_be_instantiated() {
        Value result = this.context.eval("ezs", "" +
                "class A { " +
                "    a() { " +
                "        return 'A.a'; " +
                "    } " +
                "} " +
                "new A;");

        assertTrue(result.hasMembers());
        assertEquals(Collections.emptySet(), result.getMemberKeys());
        assertTrue(result.hasMember("a"));
        Value methodA = result.getMember("a");
        assertTrue(methodA.canExecute());
        assertEquals("A.a", methodA.execute().asString());
    }

    @Test
    void methods_can_be_called_on_class_instances() {
        Value result = this.context.eval("ezs", "" +
                "class A { " +
                "    a() { " +
                "        return 'A.a'; " +
                "    } " +
                "} " +
                "new A.a();");

        assertEquals("A.a", result.asString());
    }

    @Test
    void classes_can_be_reassigned() {
        Value result = this.context.eval("ezs", "" +
                "class A { } " +
                "A = 5; " +
                "A;");

        assertEquals(5, result.asInt());
    }

    @Test
    void classes_and_objects_have_to_string() {
        Value result = this.context.eval("ezs", "" +
                "class Class { } " +
                "let c = new Class; " +
                "c + Class;");

        assertEquals("[object Object][class Class]", result.asString());
    }

    @Test
    void arguments_passed_to_new_are_evaluated() {
        Value result = this.context.eval("ezs", "" +
                "class Class { }; " +
                "let l = 3; " +
                "new Class(l = 5); " +
                "l;");

        assertEquals(5, result.asInt());
    }

    @Test
    void duplicate_methods_override_previous_ones() {
        Value result = this.context.eval("ezs", "" +
                "class Class { " +
                "   c() { return 1; } " +
                "   c() { return 2; } " +
                "} " +
                "new Class().c();");

        assertEquals(2, result.asInt());
    }

    @Test
    public void class_instances_can_be_used_as_function_arguments() {
        this.context.eval("ezs", "" +
                "class M { " +
                "    m(a) { " +
                "        return a + 1; " +
                "    } " +
                "} " +
                "function invokeM(target, argument) { " +
                "    return target.m(argument); " +
                "} " +
                "let m = new M; "
        );
        Value ezsBindings = this.context.getBindings("ezs");
        Value m = ezsBindings.getMember("m");
        Value invokeM = ezsBindings.getMember("invokeM");

        assertEquals(14, invokeM.execute(m, 13).asInt());
    }

    @Test
    void benchmark_with_alloc_inside_loop_returns_input() {
        Value result = this.context.eval("ezs", "" +
                "class Adder { " +
                "    add(a, b) { " +
                "        return a + b; " +
                "     } " +
                "} " +
                "function countForMethodPropAllocInsideLoop(n) { " +
                "    var ret = 0; " +
                "    for (let i = 0; i < n; i = i + 1) { " +
                "        ret = new Adder().add(ret, 1); " +
                "    } " +
                "    return ret; " +
                "} " +
                "countForMethodPropAllocInsideLoop(" + 1_000 + ");"
        );

        assertEquals(1_000, result.asInt());
    }

    @Test
    void benchmark_with_alloc_outside_loop_returns_input() {
        Value result = this.context.eval("ezs", "" +
                "class Adder { " +
                "    add(a, b) { " +
                "        return a + b; " +
                "     } " +
                "} " +
                "function countForMethodPropAllocOutsideLoop(n) { " +
                "    var ret = 0; " +
                "    const adder = new Adder(); " +
                "    for (let i = 0; i < n; i = i + 1) { " +
                "        ret = adder['add'](ret, 1); " +
                "    } " +
                "    return ret; " +
                "} " +
                "countForMethodPropAllocOutsideLoop(" + 1_000 + ");"
        );

        assertEquals(1_000, result.asInt());
    }

    @Test
    void classes_cannot_be_defined_inside_functions() {
        try {
            this.context.eval("ezs", "" +
                    "function f() { " +
                    "    class Class { } " +
                    "} ");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("classes nested in functions are not supported in EasyScript", e.getMessage());
        }
    }

    @Test
    void new_with_non_class_is_an_error() {
        try {
            this.context.eval("ezs",
                    "new 3();");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'3' is not a constructor", e.getMessage());
        }
    }

    @Test
    void duplicate_class_declarations_are_an_error() {
        try {
            this.context.eval("ezs", "" +
                    "class A { } " +
                    "class A { }"
            );
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("Identifier 'A' has already been declared", e.getMessage());
        }
    }
}
