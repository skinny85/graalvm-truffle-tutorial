package com.endoflineblog.truffle.part_12.nodes.exprs.strings;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.FunctionObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * An AST node that represents reading properties of strings.
 * Identical to the class with the same name from part 11.
 */
@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ReadTruffleStringPropertyNode extends EasyScriptNode {
    protected static final String LENGTH_PROP = "length";
    protected static final String CHAR_AT_PROP = "charAt";

    /** The abstract {@code execute*()} method for this node. */
    public abstract Object executeReadTruffleStringProperty(TruffleString truffleString, Object property);

    /**
     * The specialization used when accessing an integer index of a string,
     * in code like {@code "abc"[1]}.
     */
    @Specialization
    protected Object readStringIndex(
            TruffleString truffleString,
            int index,
            @Cached TruffleString.CodePointLengthNode lengthNode,
            @Cached TruffleString.SubstringNode substringNode) {
        return index < 0 || index >= EasyScriptTruffleStrings.length(truffleString, lengthNode)
                ? Undefined.INSTANCE
                : EasyScriptTruffleStrings.substring(truffleString, index, 1, substringNode);
    }

    /**
     * The specialization used when accessing the {@code length}
     * property of a string, in code like {@code "abc".length}
     * or {@code "abc"['length']}.
     */
    @Specialization(guards = "LENGTH_PROP.equals(propertyName)")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return EasyScriptTruffleStrings.length(truffleString, lengthNode);
    }

    /**
     * The specialization used when accessing the {@code charAt}
     * property of a string, in code like {@code "abc".charAt}
     * or {@code "abc"['charAt']}.
     * This is the "fast" version of the specialization,
     * which caches the created {@link FunctionObject},
     * instead of creating a new object each time the node is executed.
     */
    @Specialization(guards = {
            "CHAR_AT_PROP.equals(propertyName)",
            "same(charAtMethod.methodTarget, truffleString)"
    })
    protected FunctionObject readCharAtPropertyCached(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached("createCharAtMethodObject(truffleString)") FunctionObject charAtMethod) {
        return charAtMethod;
    }

    /**
     * The "slow" specialization version of reading the {@code charAt}
     * property that we switch to when {@link #readCharAtPropertyCached}
     * encounters more than 3 different string targets
     * (3 is the default instantiation limit for specializations in Truffle).
     * In that case, we no longer cache the {@link FunctionObject}
     * representing a given method,
     * but simply create a new object each time the node is executed.
     */
    @Specialization(guards = "CHAR_AT_PROP.equals(propertyName)", replaces = "readCharAtPropertyCached")
    protected FunctionObject readCharAtPropertyUncached(
            TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName) {
        return createCharAtMethodObject(truffleString);
    }

    protected FunctionObject createCharAtMethodObject(TruffleString truffleString) {
        return new FunctionObject(currentLanguageContext().stringPrototype.charAtMethod, 2, truffleString);
    }

    /** Accessing any other string property should return 'undefined'. */
    @Fallback
    protected Undefined readUnknownProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") Object property) {
        return Undefined.INSTANCE;
    }
}
