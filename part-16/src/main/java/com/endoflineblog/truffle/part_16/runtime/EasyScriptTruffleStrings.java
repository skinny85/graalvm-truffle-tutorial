package com.endoflineblog.truffle.part_16.runtime;

import com.endoflineblog.truffle.part_16.nodes.exprs.strings.ReadTruffleStringPropertyNode;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.api.strings.TruffleStringBuilder;

/**
 * A class containing static helper methods for dealing with {@link TruffleString}s in EasyScript.
 * Identical to the class with the same name from part 15.
 */
public final class EasyScriptTruffleStrings {
    /** The string encoding used by EasyScript - UTF-16, same as JavaScript. */
    private static final TruffleString.Encoding JAVA_SCRIPT_STRING_ENCODING = TruffleString.Encoding.UTF_16;
    public static final TruffleString EMPTY = JAVA_SCRIPT_STRING_ENCODING.getEmpty();

    public static TruffleString fromJavaString(String value) {
        return TruffleString.fromJavaStringUncached(value, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static TruffleString fromJavaString(String value, TruffleString.FromJavaStringNode fromJavaStringNode) {
        return fromJavaStringNode.execute(value, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static boolean equals(TruffleString s1, TruffleString s2,
            TruffleString.EqualNode equalNode) {
        return equalNode.execute(s1, s2, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static int length(TruffleString truffleString, TruffleString.CodePointLengthNode lengthNode) {
        return lengthNode.execute(truffleString, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static TruffleString concat(TruffleString s1, TruffleString s2, TruffleString.ConcatNode concatNode) {
        return concatNode.execute(s1, s2, JAVA_SCRIPT_STRING_ENCODING, true);
    }

    public static TruffleString substring(TruffleString truffleString, int index, int length,
            TruffleString.SubstringNode substringNode) {
        return substringNode.execute(truffleString, index, length, JAVA_SCRIPT_STRING_ENCODING, true);
    }

    public static boolean same(Object o1, Object o2) {
        return o1 == o2;
    }

    @TruffleBoundary
    public static String concatToStrings(Object o1, Object o2) {
        return o1.toString() + o2.toString();
    }

    /**
     * A method that checks whether its argument is already a {@link String}
     * before delegating to {@link #toString(Object)}.
     * Used in {@link ReadTruffleStringPropertyNode}.
     */
    public static String toStringOfMaybeString(Object object) {
        return object instanceof String
                ? (String) object
                : toString(object);
    }

    /**
     * A method that converts any value to a string.
     * Must be annotated with {@link TruffleBoundary}
     * to not interfere with partial evaluation.
     * similarly to the {@link #concatToStrings} method.
     */
    @TruffleBoundary
    public static String toString(Object object) {
        return object.toString();
    }

    /**
     * Creates a new {@link TruffleStringBuilder}
     * with the correct encoding for JavaScript (UTF-16).
     */
    public static TruffleStringBuilder builder() {
        return TruffleStringBuilder.create(JAVA_SCRIPT_STRING_ENCODING);
    }
}
