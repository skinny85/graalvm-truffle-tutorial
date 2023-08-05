package com.endoflineblog.truffle.part_12;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectsTest {
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
    public void properties_of_object_can_be_read_directly() {
        Value result = this.context.eval("ezs", "" +
                "let object = { field: 23 }; " +
                "object.field"
        );

        assertEquals(23, result.asInt());
    }

    @Test
    public void properties_of_object_can_be_read_indexed() {
        Value result = this.context.eval("ezs", "" +
                "let object = { field: 123 }; " +
                "object['field']"
        );

        assertEquals(123, result.asInt());
    }

    @Test
    public void non_string_properties_return_undefined_currently() {
        Value result = this.context.eval("ezs", "" +
                "let object = { 'undefined': 45 }; " +
                "object[undefined]"
        );

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void object_literal_is_polyglot_value() {
        Value result = this.context.eval("ezs", "" +
                "{ " +
                "    a: 3, " +
                "    'b': 4.5, " +
                "    \"c\\'\": [6, 7], " +
                "    ['d' + 'e']: { a: 4 }, " +
                "    [89]: undefined " +
                "}"
        );
        assertTrue(result.hasMembers());
        assertTrue(result.hasMember("a"));
        assertEquals(3, result.getMember("a").asInt());
        assertEquals(4.5, result.getMember("b").asDouble());
        assertEquals(2, result.getMember("c'").getArraySize());
        assertTrue(result.getMember("de").hasMember("a"));
        assertTrue(result.getMember("89").isNull());
        assertEquals(Set.of("a", "b", "c'", "de", "89"), result.getMemberKeys());
    }

    @Test
    public void benchmark_returns_correct_value() {
        int input = 1_000_000;
        Value result = this.context.eval("ezs", "" +
                "function countForObject(n) { " +
                "    let ret = 0; " +
                "    for (let i = 0; i < n; i = i + 1) { " +
                "        let obj = { ['fie' + 'ld']: i }; " +
                "        ret = ret + obj.field; " +
                "    } " +
                "    return ret; " +
                "} " +
                "countForObject(" + input + ");"
        );
        assertTrue(result.fitsInDouble());
        assertEquals(499_999_500_000D, result.asDouble());
    }
}
