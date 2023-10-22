package com.endoflineblog.truffle.part_12.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.properties.ObjectPropertyReadNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

/**
 * The Node representing reading array indexes
 * (like {@code a[1]}).
 * Identical to the class with the same name from part 11.
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    /**
     * A specialization for reading an integer index of an array,
     * in code like {@code [1, 2][1]}.
     */
    @Specialization(guards = "arrayInteropLibrary.isArrayElementReadable(array, index)", limit = "1")
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
     */
    @Specialization(guards = "propertyNameInteropLibrary.isString(propertyName)", limit = "1")
    protected Object readStringPropertyOfObject(Object target, Object propertyName,
            @CachedLibrary("propertyName") InteropLibrary propertyNameInteropLibrary,
            @Cached ObjectPropertyReadNode objectPropertyReadNode) {
        try {
            return objectPropertyReadNode.executePropertyRead(target,
                    propertyNameInteropLibrary.asString(propertyName));
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * A specialization for reading a non-string property of an object,
     * in code like {@code "a"[0]}, or {@code [1, 2][undefined]}.
     */
    @Fallback
    protected Object readNonStringPropertyOfObject(Object target, Object index,
            @Cached ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target, index);
    }
}
