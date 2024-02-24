package com.endoflineblog.truffle.part_13.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonReadPropertyNode;
import com.endoflineblog.truffle.part_13.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node representing reading array indexes
 * (like {@code a[1]}).
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    @ImportStatic(EasyScriptTruffleStrings.class)
    static abstract class InnerNode extends Node {
        abstract Object executeIndexRead(Object array, Object index);

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
         * The cached variant of the specialization for reading a string property of an object,
         * in code like {@code [1, 2]['length']}, or {@code "a"['length']}.
         */
        @Specialization(guards = "equals(propertyName, cachedPropertyName, equalNode)", limit = "2")
        protected Object readTruffleStringPropertyOfObjectCached(
                Object target,
                @SuppressWarnings("unused") TruffleString propertyName,
                @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
                @Cached("propertyName") @SuppressWarnings("unused") TruffleString cachedPropertyName,
                @Cached @SuppressWarnings("unused") TruffleString.ToJavaStringNode toJavaStringNode,
                @Cached("toJavaStringNode.execute(cachedPropertyName)") String javaStringPropertyName,
                @Cached CommonReadPropertyNode commonReadPropertyNode) {
            return commonReadPropertyNode.executeReadProperty(target, javaStringPropertyName);
        }

        /**
         * The uncached variant of the specialization for reading a string property of an object,
         * in code like {@code [1, 2]['length']}, or {@code "a"['length']}.
         */
        @Specialization(replaces = "readTruffleStringPropertyOfObjectCached")
        protected Object readTruffleStringPropertyOfObjectUncached(
                Object target, TruffleString propertyName,
                @Cached TruffleString.ToJavaStringNode toJavaStringNode,
                @Cached CommonReadPropertyNode commonReadPropertyNode) {
            return commonReadPropertyNode.executeReadProperty(target,
                    toJavaStringNode.execute(propertyName));
        }

        /**
         * A specialization for reading a non-string property of an object
         * in code like {@code a[undefined]}.
         * The index is converted to a string in that case.
         */
        @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "2")
        protected Object readNonStringProperty(Object target, Object property,
                @CachedLibrary("target") InteropLibrary interopLibrary,
                @Cached CommonReadPropertyNode commonReadPropertyNode) {
            return commonReadPropertyNode.executeReadProperty(
                    target, EasyScriptTruffleStrings.toString(property));
        }

        /**
         * A specialization for reading a non-string property of a target without members
         * (including arrays), in code like {@code "a"[0]}.
         */
        @Fallback
        protected Object readNonTruffleStringPropertyOfObject(Object target, Object index,
                @Cached CommonReadPropertyNode commonReadPropertyNode) {
            return commonReadPropertyNode.executeReadProperty(target, index);
        }
    }

    protected abstract EasyScriptExprNode getArrayExpr();
    protected abstract EasyScriptExprNode getIndexExpr();

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private InnerNode innerNode = ArrayIndexReadExprNodeGen.InnerNodeGen.create();

    @Specialization
    protected Object readIndexOrProperty(Object target, Object indexOrProperty) {
        return this.innerNode.executeIndexRead(target, indexOrProperty);
    }

    @Override
    public Object evaluateAsReceiver(VirtualFrame frame) {
        return this.getArrayExpr().executeGeneric(frame);
    }

    @Override
    public Object evaluateAsFunction(VirtualFrame frame, Object receiver) {
        Object property = this.getIndexExpr().executeGeneric(frame);
        return this.readIndexOrProperty(receiver, property);
    }
}
