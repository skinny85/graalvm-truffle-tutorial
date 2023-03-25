package com.endoflineblog.truffle.part_11.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node representing reading array indexes
 * (like {@code a[1]}).
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    /**
     * The array index syntax can also be used in JavaScript to read a property of an object
     * if the index is a string.
     * This is a specialization for {@link TruffleString}.
     */
    @Specialization
    protected Object readPropertyFromString(TruffleString target, Object index,
            @Cached ReadTruffleStringPropertyExprNode readStringPropertyExprNode) {
        return readStringPropertyExprNode.executeReadTruffleStringProperty(target,
                index);
    }

    @Specialization(guards = "arrayInteropLibrary.isArrayElementReadable(array, index)", limit = "1")
    protected Object readIntIndex(Object array, int index,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary) {
        try {
            return arrayInteropLibrary.readArrayElement(array, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * The array index syntax can also be used in JavaScript to read a property of an object
     * if the index is a string.
     */
    @Specialization(guards = {
            "targetInteropLibrary.hasMembers(target)",
            "propertyNameInteropLibrary.isString(propertyName)"
    }, limit = "1")
    protected Object readProperty(Object target, Object propertyName,
            @CachedLibrary("target") InteropLibrary targetInteropLibrary,
            @CachedLibrary("propertyName") InteropLibrary propertyNameInteropLibrary) {
        try {
            return targetInteropLibrary.readMember(target,
                    propertyNameInteropLibrary.asString(propertyName));
        } catch (UnknownIdentifierException e) {
            return Undefined.INSTANCE;
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    @Specialization(guards = "interopLibrary.isNull(target)", limit = "1")
    protected Object indexUndefined(@SuppressWarnings("unused") Object target,
            Object index,
            @SuppressWarnings("unused") @CachedLibrary("target") InteropLibrary interopLibrary) {
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + index + "')");
    }

    @Fallback
    protected Object readNonArrayOrNonIntIndex(@SuppressWarnings("unused") Object array,
            @SuppressWarnings("unused") Object index) {
        return Undefined.INSTANCE;
    }
}
