package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlusTest {
    private Context context;
    private Value addFunc;

    @BeforeEach
    public void setUp() {
        this.context = Context.create();
        this.addFunc = this.context.eval("ezs", "" +
                "function add(a, b) { " +
                "    return a + b; " +
                "} " +
                "add; "
        );
    }

    @AfterEach
    public void tearDown() {
        this.context.close();
    }

    @Test
    public void concat_then_int_overflow_works() {
        Value str = addFunc.execute("a", "b");
        assertEquals("ab", str.asString());

        Value i = addFunc.execute(1, 2);
        assertEquals(3, i.asInt());

        Value d = addFunc.execute(Integer.MAX_VALUE, 1);
        assertEquals(Integer.MAX_VALUE + 1D, d.asDouble());
    }
}
