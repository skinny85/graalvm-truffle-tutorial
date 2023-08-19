package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                "class A { " +
                "    a() { " +
                "        return 'A.a'; " +
                "    } " +
                "} " +
                "A;");

        assertEquals("[class A]", result.toString());
    }
}
