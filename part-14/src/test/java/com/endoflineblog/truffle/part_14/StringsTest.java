package com.endoflineblog.truffle.part_14;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This is a set of unit tests for testing strings in EasyScript.
 */
class StringsTest {
    private Context context;

    @BeforeEach
    void setUp() {
        this.context = Context.newBuilder()
                .option("cpusampler", "true")
                .build();
    }

    @AfterEach
    void tearDown() {
        this.context.close();
    }

    @Test
    void strings_can_be_created_with_single_quotes() {
        Value result = this.context.eval("ezs",
                " '' "
        );
        assertTrue(result.isString());
        assertEquals("", result.asString());
    }

    @Test
    void single_quote_strings_can_contain_a_single_quote_by_escaping_it() {
        Value result = this.context.eval("ezs",
                " '\\'' "
        );
        assertTrue(result.isString());
        assertEquals("'", result.asString());
    }

    @Test
    void strings_can_be_created_with_double_quotes() {
        Value result = this.context.eval("ezs",
                " \"\" "
        );
        assertTrue(result.isString());
        assertEquals("", result.asString());
    }

    @Test
    void double_quote_strings_can_contain_a_double_quote_by_escaping_it() {
        Value result = this.context.eval("ezs",
                " \"\\\"\" "
        );
        assertTrue(result.isString());
        assertEquals("\"", result.asString());
    }

    @Test
    void empty_string_is_falsy() {
        Value result = this.context.eval("ezs", "" +
                "let ret; " +
                "if ('') { " +
                "    ret = 'empty string is truthy'; " +
                "} else { " +
                "    ret = 'empty string is falsy'; " +
                "} " +
                "ret"
        );
        assertEquals("empty string is falsy", result.asString());
    }

    @Test
    void blank_string_is_truthy() {
        Value result = this.context.eval("ezs", "" +
                "let ret; " +
                "if (' ') { " +
                "    ret = 'blank string is truthy'; " +
                "} else { " +
                "    ret = 'blank string is falsy'; " +
                "} " +
                "ret"
        );
        assertEquals("blank string is truthy", result.asString());
    }

    @Test
    void strings_can_be_concatenated() {
        Value result = this.context.eval("ezs",
                " 'abc' + '_' + 'def' "
        );
        assertEquals("abc_def", result.asString());
    }

    @Test
    void properties_can_be_accessed_through_indexing() {
        Value result = this.context.eval("ezs", "" +
                "const arr = [0, 1, 2]; " +
                "arr['length']"
        );
        assertEquals(3, result.asInt());
    }

    @Test
    void strings_have_a_length_property() {
        Value result = this.context.eval("ezs",
                " 'length'.length"
        );
        assertEquals(6, result.asInt());
    }

    @Test
    void string_properties_can_be_accessed_through_indexing() {
        Value result = this.context.eval("ezs", "" +
                "var l = 'length'; " +
                "l[l]"
        );
        assertEquals(6, result.asInt());
    }

    @Test
    void indexed_property_writes_to_strings_have_no_effect() {
        Value result = this.context.eval("ezs", "" +
                "const str = 'abc'; " +
                "const result = str['length'] = 5; " +
                "[result, str.length]"
        );
        assertEquals(5, result.getArrayElement(0).asInt());
        assertEquals(3, result.getArrayElement(1).asInt());
    }

    @Test
    void ezs_strings_in_polyglot_context_have_no_members() {
        Value result = this.context.eval("ezs", " 'a' ");

        assertTrue(result.isString());
        assertFalse(result.hasMembers());
    }

    @Test
    void java_strings_can_be_used_for_indexing() {
        this.context.eval("ezs", "" +
                "function access(o, propertyName) { " +
                "    return o[propertyName]; " +
                "} " +
                "let str = 'ab';"
        );
        Value ezsBindings = this.context.getBindings("ezs");
        Value str = ezsBindings.getMember("str");
        Value access = ezsBindings.getMember("access");

        assertEquals(2, access.execute(str, "length").asInt());
    }

    @Test
    void strings_can_be_indexed() {
        Value result = this.context.eval("ezs",
                " 'abc'[1][0]"
        );
        assertEquals("b", result.asString());
    }

    @Test
    void strings_indexed_out_of_range_return_undefined() {
        Value result = this.context.eval("ezs",
                " 'abc'[-1]"
        );
        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    void strings_can_be_compared_for_equality() {
        Value result = this.context.eval("ezs", "" +
                "let ret = 'string equality is broken'; " +
                "if ('abc' === 'a' + 'b' + 'c') " +
                "    ret = 'string equality works correctly'; " +
                "ret"
        );
        assertEquals("string equality works correctly", result.asString());
    }

    @Test
    void strings_can_be_compared_with_less() {
        Value result = this.context.eval("ezs",
                " 'a' < 'b' "
        );
        assertTrue(result.asBoolean());
    }

    @Test
    void concatenated_strings_have_a_length_property() {
        Value result = this.context.eval("ezs",
                "('abc' + 'def').length"
        );
        assertEquals(6, result.asInt());
    }

    @Test
    void strings_have_a_charAt_method() {
        Value result = this.context.eval("ezs",
                " 'abc'.charAt(2)"
        );
        assertEquals("c", result.asString());
    }

    @Test
    void charAt_without_argument_defaults_to_0() {
        Value result = this.context.eval("ezs",
                " 'abc'.charAt()"
        );
        assertEquals("a", result.asString());
    }

    @Test
    void charAt_outside_range_returns_empty_string() {
        Value result = this.context.eval("ezs",
                " 'abc'.charAt(3) + 'abc'.charAt(-1)"
        );
        assertEquals("", result.asString());
    }

    @Test
    void charAt_called_on_an_empty_string_without_arguments_returns_empty_string() {
        Value result = this.context.eval("ezs",
                " ''.charAt()"
        );
        assertEquals("", result.asString());
    }

    @Test
    void methods_ignore_extra_arguments() {
        Value result = this.context.eval("ezs",
                " 'abc'.charAt(1, 2, 99)"
        );
        assertEquals("b", result.asString());
    }

    @Test
    void unknown_string_property_returns_undefined() {
        Value result = this.context.eval("ezs", " 'a'.someProp");

        assertTrue(result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    void unknown_string_member_read_through_GraalVM_interop_throws() {
        Value str = this.context.eval("ezs", "'my-string'");
        assertFalse(str.hasMembers());
        try {
            str.getMember("doesNotExist");
            fail("expected String.getMember() to throw");
        } catch (UnsupportedOperationException e) {
            // nothing to do here
        }
    }

    @Test
    void methods_correctly_resolve_their_targets() {
        Value result = this.context.eval("ezs", "" +
                "function firstChar(str) { " +
                "    return str.charAt(0); " +
                "} " +
                "firstChar('A'); " +
                "firstChar('B'); " +
                "firstChar('C'); " +
                "firstChar('D');"
        );
        assertEquals("D", result.asString());
    }

    @Test
    void string_properties_work_after_reading_non_existing_property() {
        this.context.eval("ezs", "" +
                "function readProp(str, prop) { " +
                "    return str[prop]; " +
                "} " +
                "let v1 = readProp('', 'does not exist'); " +
                "let v2 = readProp('', 'length');");

        Value ezsBindings = this.context.getBindings("ezs");
        Value v1 = ezsBindings.getMember("v1");
        Value v2 = ezsBindings.getMember("v2");
        assertTrue(v1.isNull());
        assertEquals(0, v2.asInt());
    }

    @Test
    void chatAt_works_after_passing_it_undefined() {
        Value charAtStr = this.context.eval("ezs", "" +
                "function charAtStr(index) { " +
                "    return 'str'.charAt(index); " +
                "} " +
                "charAtStr;");

        assertEquals("s", charAtStr.execute().asString());
        assertEquals("t", charAtStr.execute(1).asString());
        assertEquals("r", charAtStr.execute(2).asString());
    }

    @Test
    void Math_props_can_be_accessed_through_indexing() {
        Value result = this.context.eval("ezs",
                "Math['abs'](-4)"
        );
        assertEquals(4, result.asInt());
    }

    @Test
    void string_index_writes_are_ignored() {
        Value result = this.context.eval("ezs", "" +
                "let s = 'a'; " +
                "const tmp = s[0] = 'b'; " +
                "s + tmp"
        );
        assertEquals("ab", result.asString());
    }

    @Test
    void count_algorithm_returns_its_input() {
        int input = 10_000;
        Value result = this.context.eval("ezs", "" +
                "function countWhileCharAtIndexProp(n) { " +
                "    var ret = 0; " +
                "    while (n > 0) { " +
                "        n = n - ('a'['charAt'](0) + ''['charAt']())['length']; " +
                "        ret = ret + 1; " +
                "    } " +
                "    return ret; " +
                "}" +
                "countWhileCharAtIndexProp(" + input + ");"
        );

        assertEquals(input, result.asInt());
    }
}
