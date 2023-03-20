package com.endoflineblog.truffle.part_11.nodes.exprs.strings;

import com.endoflineblog.truffle.part_11.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_11.runtime.FunctionObject;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

import static com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings.fromJavaString;

@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ReadTruffleStringPropertyExprNode extends EasyScriptNode {
    protected static final String LENGTH_PROP = "length";
    protected static final TruffleString LENGTH_PROP_TS = fromJavaString(LENGTH_PROP);
    protected static final String CHAR_AT_PROP = "charAt";
    protected static final TruffleString CHAR_AT_PROP_TS = fromJavaString(CHAR_AT_PROP);

    public abstract Object executeReadTruffleStringProperty(TruffleString truffleString, Object property);

    @Specialization
    protected Object readArrayIndex(
            TruffleString truffleString,
            int index,
            @Cached TruffleString.CodePointLengthNode lengthNode,
            @Cached TruffleString.SubstringNode substringNode) {
        return index < 0 || index >= EasyScriptTruffleStrings.length(truffleString, lengthNode)
                ? Undefined.INSTANCE
                : EasyScriptTruffleStrings.substring(truffleString, index, 1, substringNode);
    }

    @Specialization(guards = "LENGTH_PROP.equals(propertyName)")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return EasyScriptTruffleStrings.length(truffleString, lengthNode);
    }

    @Specialization(guards = "equals(LENGTH_PROP_TS, propertyName, equalNode)")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return EasyScriptTruffleStrings.length(truffleString, lengthNode);
    }

    @Specialization(guards = {
            "CHAR_AT_PROP.equals(propertyName)",
            "same(charAtMethod.methodTarget, truffleString)"
    })
    protected FunctionObject readCharAtProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached("create(currentLanguageContext().stringPrototype.charAtMethod, 2, truffleString)") FunctionObject charAtMethod) {
        return charAtMethod;
    }

    @Specialization(guards = {
            "equals(CHAR_AT_PROP_TS, propertyName, equalNode)",
            "same(charAtMethod.methodTarget, truffleString)"
    })
    protected FunctionObject readCharAtProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
            @Cached("create(currentLanguageContext().stringPrototype.charAtMethod, 2, truffleString)") FunctionObject charAtMethod) {
        return charAtMethod;
    }

    /** Accessing any other string property should return 'undefined'. */
    @Specialization
    protected Undefined readUnknownProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") Object property) {
        return Undefined.INSTANCE;
    }
}
