package com.endoflineblog.truffle.part_13.nodes.exprs.strings;

import com.endoflineblog.truffle.part_13.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_13.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_13.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * An AST node that represents reading properties of strings.
 * Similar to the class with the same name from part 12,
 * the main difference being we read properties directly from the string prototype,
 * accessed through the {@link com.endoflineblog.truffle.part_13.common.ShapesAndPrototypes}
 * class, instead of having two separate specializations per property.
 *
 * @see #readNonLengthProperty
 */
public abstract class ReadTruffleStringPropertyNode extends EasyScriptNode {
    protected static final String LENGTH_PROP = "length";

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

    @Fallback
    protected Object readNonLengthProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            Object property,
            @Cached("currentLanguageContext().shapesAndPrototypes.stringPrototype") ClassPrototypeObject stringPrototype,
            @CachedLibrary(limit = "2") DynamicObjectLibrary stringPrototypeObjectLibrary) {
        return stringPrototypeObjectLibrary.getOrDefault(stringPrototype, property,
                Undefined.INSTANCE);
    }
}
