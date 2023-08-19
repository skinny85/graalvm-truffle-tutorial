package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ClassTest {
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
    }

    @Test
    void new_with_non_class_is_an_error() {
        try {
            this.context.eval("ezs",
                    "new 3;");
            fail("expected PolyglotException to be thrown");
        } catch (PolyglotException e) {
            assertTrue(e.isGuestException());
            assertFalse(e.isInternalError());
            assertEquals("'3' is not a constructor", e.getMessage());
        }
    }
}
