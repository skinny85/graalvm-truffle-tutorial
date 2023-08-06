package com.endoflineblog.truffle.part_12.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyReadNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.dsl.Fallback;
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
    @Specialization(guards = "arrayInteropLibrary.isArrayElementReadable(array, index)", limit = "1")
    protected Object readIntArrayIndex(Object array, int index,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary) {
        try {
            return arrayInteropLibrary.readArrayElement(array, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * A specialization for reading a Java string property of any object.
     * We need to convert the property name from a Java String to a {@link TruffleString},
     * which is what {@link ObjectPropertyReadNode} expects.
     */
    @Specialization
    protected Object readJavaStringProperty(Object target, String propertyName,
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode,
            @Cached @Shared("objectPropertyReadNode") ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target,
                EasyScriptTruffleStrings.fromJavaString(propertyName, fromJavaStringNode));
    }

    /**
     * A specialization for reading a string property of a non-string target,
     * in code like {@code [1, 2]['length']}.
     * The implementation is identical to {@code PropertyReadExprNode}.
     */
    @Fallback
    protected Object readProperty(Object target, Object property,
            @Cached @Shared("objectPropertyReadNode") ObjectPropertyReadNode objectPropertyReadNode) {
        return objectPropertyReadNode.executePropertyRead(target, property);
    }
}
