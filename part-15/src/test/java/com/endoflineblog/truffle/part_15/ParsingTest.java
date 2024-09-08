package com.endoflineblog.truffle.part_15;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This is a set of unit tests for validating parsing edge cases
 * (mainly relating to the {@code new} operator) in EasyScript.
 */
public class ParsingTest {
    private Context context;

    @BeforeEach
    public void setUp() {
        this.context = Context.create();
    }

    @AfterEach
    public void tearDown() {
        this.context.close();
    }

    @Test
    public void multiple_calls_of_calls_are_parsed() {
        this.context.parse("ezs", "a()()()");
    }

    @Test
    public void property_reads_of_property_reads_are_parsed() {
        this.context.parse("ezs", "a.b.c");
    }

    @Test
    public void method_calls_of_calls_are_parsed() {
        this.context.parse("ezs", "a().b().c()");
    }
}
