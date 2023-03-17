package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.strings.AbstractTruffleString;
import com.oracle.truffle.api.strings.TruffleString;

public final class EasyScriptTruffleStrings {
    private static final TruffleString.Encoding JAVA_SCRIPT_STRING_ENCODING = TruffleString.Encoding.UTF_16;

    public static TruffleString fromJavaString(String value) {
        return TruffleString.fromJavaStringUncached(value, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static TruffleString fromJavaString(String value, TruffleString.FromJavaStringNode fromJavaStringNode) {
        return fromJavaStringNode.execute(value, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static boolean equals(AbstractTruffleString s1, AbstractTruffleString s2, TruffleString.EqualNode equalNode) {
        return equalNode.execute(s1, s2, JAVA_SCRIPT_STRING_ENCODING);
    }

    public static boolean same(Object o1, Object o2) {
        return o1 == o2;
    }

    public static TruffleString concat(TruffleString s1, TruffleString s2, TruffleString.ConcatNode concatNode) {
        return concatNode.execute(s1, s2, JAVA_SCRIPT_STRING_ENCODING, true);
    }

    @TruffleBoundary
    public static String concatToStrings(Object o1, Object o2) {
        return o1.toString() + o2.toString();
    }
}
