package com.endoflineblog.truffle.part_12.nodes.exprs.strings;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.FunctionObject;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * An AST node that represents reading properties of strings.
 * Since EasyScript uses the Truffle {@link TruffleString}
 * type as its string representation,
 * we don't want to wrap it in another {@code TruffleObject},
 * like {@code ArrayObject}.
 * For that reason, we need this dedicated Node type to read string properties.
 * Note that this class does not extend {@code EasyScriptExprNode},
 * similarly to how {@code FunctionDispatchNode} works,
 * so it can define its own {@link #executeReadTruffleStringProperty execute*() method}.
 *
 * @see #executeReadTruffleStringProperty
 * @see #readStringIndex
 * @see #readLengthProperty
 * @see #readCharAtPropertyCached
 * @see #readCharAtPropertyUncached
 * @see #readNonIntNonStringProperty
 */
@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ReadTruffleStringPropertyExprNode extends EasyScriptNode {
    protected static final TruffleString LENGTH_PROP = EasyScriptTruffleStrings.fromJavaString("length");

    /** The abstract {@code execute*()} method for this node. */
    public abstract Object executeReadTruffleStringProperty(TruffleString truffleString, Object property);

    /**
     * The specialization used when accessing an integer index of a string,
     * in code like {@code "abc"[1]}.
     */
    @Specialization
    protected Object readStringIndex(
            TruffleString truffleString, int index,
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
    @Specialization(guards = "equals(LENGTH_PROP, propertyName, equalNode)", limit = "1")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @SuppressWarnings("unused") @Cached TruffleString.EqualNode equalNode,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return EasyScriptTruffleStrings.length(truffleString, lengthNode);
    }

    @Specialization(guards = "!equals(LENGTH_PROP, propertyName, equalNode)", limit = "1")
    protected Object readNonLengthProperty(
            TruffleString truffleString,
            TruffleString propertyName,
            @SuppressWarnings("unused") @Cached TruffleString.EqualNode equalNode,
            @CachedLibrary(limit = "1") DynamicObjectLibrary objectLibrary) {
        JavaScriptObject stringPrototype = this.currentLanguageContext().stringPrototype;
        Object propertyValue = objectLibrary.getOrDefault(stringPrototype, propertyName, null);
        if (propertyValue == null) {
            return Undefined.INSTANCE;
        }
        return propertyValue instanceof CallTarget
                // ToDo where do we get the number of arguments from???
                ? new FunctionObject((CallTarget) propertyValue, 2, truffleString)
                : propertyValue;
    }

    /** Accessing any other string property should return 'undefined'. */
    @Fallback
    protected Undefined readNonIntNonStringProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") Object property) {
        return Undefined.INSTANCE;
    }
}
