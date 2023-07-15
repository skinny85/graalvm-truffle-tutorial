package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlusAssignmentTest {
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
    public void plus_assignment_works() {
        Value addTwo = this.context.eval("ezs", "" +
                "function addTwo(n) { " +
                "    let local = 2; " +
                "    local += n; " +
                "    return local; " +
                "} " +
                "addTwo; "
        );

        assertEquals(5, addTwo.execute(3).asInt());
        assertEquals("2ab", addTwo.execute("ab").asString());
//        assertTrue(addTwo.execute(true).isNull());
    }
}
