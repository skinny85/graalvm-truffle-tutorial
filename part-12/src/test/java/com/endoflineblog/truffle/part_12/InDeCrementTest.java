package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InDeCrementTest {
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
    void postfix_increment_works_for_local_variables() {
        Value result = this.context.eval("ezs", "" +
                "function a() { " +
                "    let local = 1; " +
                "    local++; " +
                "    return local; " +
                "} " +
                "a(); ");

        assertEquals(2, result.asInt());
    }
}
