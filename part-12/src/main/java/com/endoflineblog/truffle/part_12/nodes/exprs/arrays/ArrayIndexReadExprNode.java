package com.endoflineblog.truffle.part_12.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
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
 * Similar to the class with the same name from part 10,
 * the difference is we add extra specializations for handling strings -
 * both as targets of the indexing (where we expect {@link TruffleString}s),
 * and when strings are used as the index -
 * in code like {@code a["b"]}, which in JavaScript is equivalent to {@code a.b}.
 *
 * @see #readStringPropertyOfString
 * @see #readPropertyOfString
 * @see #readProperty
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    /**
     * A specialization for reading a string property of a string,
     * in code like {@code "a"['length']}.
     * We delegate to {@link ReadTruffleStringPropertyExprNode},
     * but we first convert the index to a Java string,
     * which is what {@link ReadTruffleStringPropertyExprNode} expects.
     */
    @Specialization(limit = "1", guards = "indexInteropLibrary.isString(index)")
    protected Object readStringPropertyOfString(TruffleString target,
            Object index,
            @CachedLibrary("index") InteropLibrary indexInteropLibrary,
            @Cached ReadTruffleStringPropertyExprNode readStringPropertyExprNode) {
        try {
            return readStringPropertyExprNode.executeReadTruffleStringProperty(target,
                    indexInteropLibrary.asString(index));
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * A specialization for reading a non-string property of a string.
     * The main usecase for this is string indexing,
     * in code like {@code "a"[1]}.
     * We delegate the implementation to {@link ReadTruffleStringPropertyExprNode}.
     */
    @Specialization
    protected Object readPropertyOfString(TruffleString target, Object index,
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
     * A specialization for reading a string property of a non-string target,
     * in code like {@code [1, 2]['length']}.
     * The implementation is identical to {@code PropertyReadExprNode}.
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
