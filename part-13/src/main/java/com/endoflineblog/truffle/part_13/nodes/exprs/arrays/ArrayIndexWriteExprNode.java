package com.endoflineblog.truffle.part_13.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonWritePropertyNode;
import com.endoflineblog.truffle.part_13.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node representing writing array indexes
 * (like {@code a[1] = 3}).
 * Very similar to the class with the same name from part 12,
 * the main difference is that it needs to also handle property writes
 * when the index is a string, in code like {@code obj["foo"] = value},
 * which we handle by delegating to the {@link CommonWritePropertyNode}.
 * It also caches the Java {@link String} converted from a {@link TruffleString}
 * provided as the index, similarly to {@link ArrayIndexReadExprNode}.
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
@NodeChild("rvalueExpr")
@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ArrayIndexWriteExprNode extends EasyScriptExprNode {
    /**
     * A specialization for writing an integer index of an array,
     * in code like {@code [1, 2][1] = 3}.
     */
    @Specialization(guards = "arrayInteropLibrary.isArrayElementWritable(array, index)", limit = "2")
    protected Object writeIntIndexOfArray(
            Object array, int index, Object rvalue,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary) {
        try {
            arrayInteropLibrary.writeArrayElement(array, index, rvalue);
        } catch (UnsupportedMessageException | InvalidArrayIndexException | UnsupportedTypeException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
        return rvalue;
    }

    /**
     * The cached version of the specialization for writing a string property of an object,
     * in code like {@code [1, 2]['abc'] = 3}.
     */
    @Specialization(guards = "equals(propertyName, cachedPropertyName, equalNode)", limit = "2")
    protected Object writeTruffleStringPropertyCached(
            Object target, TruffleString propertyName, Object rvalue,
            @Cached("propertyName") @SuppressWarnings("unused") TruffleString cachedPropertyName,
            @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
            @Cached @SuppressWarnings("unused") TruffleString.ToJavaStringNode toJavaStringNode,
            @Cached("toJavaStringNode.execute(propertyName)") String javaStringPropertyName,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        return commonWritePropertyNode.executeWriteProperty(target,
                javaStringPropertyName, rvalue);
    }

    /**
     * The uncached version of the specialization for writing a string property of an object,
     * in code like {@code [1, 2]['abc'] = 3}.
     */
    @Specialization(replaces = "writeTruffleStringPropertyCached")
    protected Object writeTruffleStringPropertyUncached(
            Object target, TruffleString propertyName, Object rvalue,
            @Cached TruffleString.ToJavaStringNode toJavaStringNode,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        return commonWritePropertyNode.executeWriteProperty(target,
                toJavaStringNode.execute(propertyName), rvalue);
    }

    /**
     * A specialization for writing a non-string property of an object,
     * in code like {@code myObj[undefined] = "a"}, or {@code "a"[0] = 3}.
     * The index is converted to a string in that case.
     */
    @Fallback
    protected Object writeNonStringProperty(
            Object target, Object property, Object rvalue,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        return commonWritePropertyNode.executeWriteProperty(target,
                EasyScriptTruffleStrings.toString(property), rvalue);
    }
}
