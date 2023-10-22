package com.endoflineblog.truffle.part_13.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonWritePropertyNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;

/**
 * The Node representing writing array indexes
 * (like {@code a[1] = 3}).
 * Identical to the class with the same name from part 11.
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
@NodeChild("rvalueExpr")
public abstract class ArrayIndexWriteExprNode extends EasyScriptExprNode {
    @Specialization(guards = "arrayInteropLibrary.isArrayElementWritable(array, index)", limit = "2")
    protected Object writeIntIndexOfArray(Object array, int index, Object rvalue,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary) {
        try {
            arrayInteropLibrary.writeArrayElement(array, index, rvalue);
        } catch (UnsupportedMessageException | InvalidArrayIndexException | UnsupportedTypeException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
        return rvalue;
    }

    /**
     * A specialization for writing a string property of an object,
     * in code like {@code [1, 2]['abc'] = 3}.
     */
    @Specialization(guards = "propertyNameInteropLibrary.isString(propertyName)", limit = "2")
    protected Object writeStringPropertyOfObject(Object target, Object propertyName, Object rvalue,
            @CachedLibrary("propertyName") InteropLibrary propertyNameInteropLibrary,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        try {
            return commonWritePropertyNode.executeWriteProperty(target,
                    propertyNameInteropLibrary.asString(propertyName), rvalue);
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * A specialization for writing a non-string property of an object,
     * in code like {@code "a"[0]}, or {@code [1, 2][undefined]}.
     */
    @Fallback
    protected Object writeNonStringPropertyOfObject(Object target, Object index, Object rvalue,
            @Cached CommonWritePropertyNode commonWritePropertyNode) {
        return commonWritePropertyNode.executeWriteProperty(target, index, rvalue);
    }
}
