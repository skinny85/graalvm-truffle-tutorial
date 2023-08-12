package com.endoflineblog.truffle.part_12.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyWriteNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyWriteNodeGen;
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
 * Identical to the class with the same name from part 10.
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
@NodeChild("rvalueExpr")
public abstract class ArrayIndexWriteExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ObjectPropertyWriteNode objectPropertyWriteNode;

    protected ArrayIndexWriteExprNode() {
        this.objectPropertyWriteNode = ObjectPropertyWriteNodeGen.create();
    }

    @Specialization(guards = "arrayInteropLibrary.isArrayElementWritable(array, index)", limit = "1")
    protected Object writeIntIndex(Object array, int index, Object rvalue,
            @CachedLibrary("array") InteropLibrary arrayInteropLibrary) {
        try {
            arrayInteropLibrary.writeArrayElement(array, index, rvalue);
        } catch (UnsupportedMessageException | InvalidArrayIndexException | UnsupportedTypeException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
        return rvalue;
    }

    @Fallback
    protected Object writeNonArray(Object object, Object property, Object rvalue) {
        return this.objectPropertyWriteNode.executePropertyWrite(object, property, rvalue);
    }
}
