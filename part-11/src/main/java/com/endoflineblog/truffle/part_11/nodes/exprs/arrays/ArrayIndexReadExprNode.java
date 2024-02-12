package com.endoflineblog.truffle.part_11.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.properties.ObjectPropertyReadNode;
import com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node representing reading array indexes
 * (like {@code a[1]}).
 * Similar to the class with the same name from part 10,
 * the difference is we add extra specializations for when strings are used as the index,
 * in code like {@code a["b"]} (which, in JavaScript, is equivalent to {@code a.b}).
 *
 * @see #readTruffleStringPropertyOfObjectCached
 * @see #readTruffleStringPropertyOfObjectUncached
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    /**
     * A specialization for reading an integer index of an array,
     * in code like {@code [1, 2][1]}.
     */
    @Specialization(guards = "arrayInteropLibrary.isArrayElementReadable(array, index)", limit = "2")
    protected Object readIntIndexOfArray(Object array, int index,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary) {
        try {
            return arrayInteropLibrary.readArrayElement(array, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * A specialization for reading a string property of an object,
     * in code like {@code [1, 2]['length']}, or {@code "a"['length']}.
     * This is the cached variant of the specialization,
     * where the result of converting the {@link TruffleString}
     * representing the property name to a Java String is saved,
     * for a maximum of two different names.
     * If the given indexed property access sees more than two different names,
     * then we switch to the uncached variant,
     * {@link #readTruffleStringPropertyOfObjectUncached}.
     */
    @Specialization(guards = "equals(propertyName, cachedPropertyName, equalNode)", limit = "2")
    protected Object readTruffleStringPropertyOfObjectCached(
            Object target,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
            @Cached("propertyName") @SuppressWarnings("unused") TruffleString cachedPropertyName,
            @Cached @SuppressWarnings("unused") TruffleString.ToJavaStringNode toJavaStringNode,
            @Cached("toJavaStringNode.execute(cachedPropertyName)") String javaStringPropertyName,
            @Cached ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target, javaStringPropertyName);
    }

    /**
     * A specialization for reading a string property of an object,
     * in code like {@code [1, 2]['length']}, or {@code "a"['length']}.
     * This is the uncached variant of the {@link #readTruffleStringPropertyOfObjectCached}
     * specialization, used when this indexed property access sees more than two different property names.
     */
    @Specialization(replaces = "readTruffleStringPropertyOfObjectCached")
    protected Object readTruffleStringPropertyOfObjectUncached(Object target, TruffleString propertyName,
            @Cached TruffleString.ToJavaStringNode toJavaStringNode,
            @Cached ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target,
                toJavaStringNode.execute(propertyName));
    }

    /**
     * A specialization for reading a non-string property of an object,
     * in code like {@code "a"[0]}, or {@code [1, 2][undefined]}.
     */
    @Fallback
    protected Object readNonTruffleStringPropertyOfObject(Object target, Object index,
            @Cached ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target, index);
    }
}
