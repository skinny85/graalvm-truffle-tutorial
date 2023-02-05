package com.endoflineblog.truffle.part_11;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This is a set of unit tests for testing strings in EasyScript.
 */
public class StringsTest {
    private Context context;

    @Before
    public void setUp() {
        this.context = Context.create();
    }

    @After
    public void tearDown() {
        this.context.close();
    }

    @Test
    public void strings_can_be_created_with_single_quotes() {
        Value result = this.context.eval("ezs",
                "''"
        );
        assertTrue(result.isString());
        assertEquals("", result.asString());
    }

    @Test
    public void single_quote_strings_can_contain_a_single_quote_by_escaping_it() {
        Value result = this.context.eval("ezs",
                "'\\''"
        );
        assertTrue(result.isString());
        assertEquals("'", result.asString());
    }

    @Test
    public void empty_string_is_falsy() {
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
    public void blank_string_is_truthy() {
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
    public void strings_can_be_concatenated() {
        Value result = this.context.eval("ezs",
                "'abc' + '_' + 'def'"
        );
        assertEquals("abc_def", result.asString());
    }

    @Test
    public void properties_can_be_accessed_through_indexing() {
        Value result = this.context.eval("ezs", "" +
                "const arr = [0, 1, 2]; " +
                "arr['length']"
        );
        assertEquals(3, result.asInt());
    }

    @Test
    public void property_writes_through_indexing_are_ignored() {
        Value result = this.context.eval("ezs", "" +
                "const arr = [0, 1, 2]; " +
                "const result = arr['length'] = 5;" +
                "[result, arr.length]"
        );
        assertEquals(5, result.getArrayElement(0).asInt());
        assertEquals(3, result.getArrayElement(1).asInt());
    }

    @Test
    public void strings_have_a_length_property() {
        Value result = this.context.eval("ezs",
                "'length'.length"
        );
        assertEquals(6, result.asInt());
    }

    @Test
    public void java_strings_can_be_used_for_indexing() {
        this.context.eval("ezs", "" +
                "function access(o, propertyName) { " +
                "    return o[propertyName]; " +
                "} " +
                "let str = 'ab'; "
        );
        Value ezsBindings = this.context.getBindings("ezs");
        Value str = ezsBindings.getMember("str");
        Value access = ezsBindings.getMember("access");

        assertEquals(2, access.execute(str, "length").asInt());
    }

    @Test
    public void strings_can_be_indexed() {
        Value result = this.context.eval("ezs",
                "'abc'[1][0]"
        );
        assertEquals("b", result.asString());
    }

    @Test
    public void strings_can_be_compared_for_equality() {
        Value result = this.context.eval("ezs", "" +
                "let ret = 'string equality is broken'; " +
                "if ('abc' === 'a' + 'b' + 'c') " +
                "    ret = 'string equality works correctly'; " +
                "ret"
        );
        assertEquals("string equality works correctly", result.asString());
    }

    @Test
    public void concatenated_strings_have_a_length_property() {
        Value result = this.context.eval("ezs",
                "('abc' + 'def').length"
        );
        assertEquals(6, result.asInt());
    }

    @Test
    public void strings_have_a_codeAt_method() {
        Value result = this.context.eval("ezs",
                "'abc'.charAt(2)"
        );
        assertEquals("c", result.asString());
    }

    @Test
    public void codeAt_without_argument_defaults_to_0() {
        Value result = this.context.eval("ezs",
                "'abc'.charAt()"
        );
        assertEquals("a", result.asString());
    }

    @Test
    public void strings_have_a_substring_method() {
        Value result = this.context.eval("ezs",
                "'abc'.substring(1, 2)"
        );
        assertEquals("b", result.asString());
    }

    @Test
    public void methods_ignore_extra_arguments() {
        Value result = this.context.eval("ezs",
                "'abc'.substring(1, 2, 99)"
        );
        assertEquals("b", result.asString());
    }

    private final int fastaInput = 1_400_000;

    @Test
    public void fasta_repeat_returns_its_input() {
        Value result = this.context.eval("ezs", "" +
                FastaCode.FASTA_PROGRAM +
                "fastaRepeat(" + fastaInput + ");"
        );
        assertEquals(fastaInput, result.asInt());
    }

    @Test
    public void fasta_repeat_no_substring_returns_its_input() {
        Value result = this.context.eval("ezs", "" +
                FastaCode.FASTA_PROGRAM_NO_SUBSTRING +
                "fastaRepeatNoSubstring(" + fastaInput + ");"
        );
        assertEquals(fastaInput, result.asInt());
    }

    @Test
    public void fasta_repeat_without_length_returns_its_input() {
        Value result = this.context.eval("ezs", "" +
                FastaCode.FASTA_PROGRAM_WITHOUT_LENGTH +
                "fastaRepeatWithoutLength(" + fastaInput + ");"
        );
        assertEquals(fastaInput, result.asInt());
    }

    @Test
    public void java_fasta_repeat_returns_its_input() {
        assertEquals(fastaInput, FastaCode.fastaRepeat(fastaInput));
    }

    @Test
    public void java_fasta_repeat_no_substring_returns_its_input() {
        assertEquals(fastaInput, FastaCode.fastaRepeatNoSubstring(fastaInput));
    }

    @Test
    public void java_fasta_repeat_without_length_returns_its_input() {
        assertEquals(fastaInput, FastaCode.fastaRepeatWithoutLength(fastaInput));
    }
}
