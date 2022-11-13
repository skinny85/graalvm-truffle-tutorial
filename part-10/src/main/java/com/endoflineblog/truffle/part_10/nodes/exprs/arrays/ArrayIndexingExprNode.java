package com.endoflineblog.truffle.part_10.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.runtime.Undefined;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

@NodeChild("arrayExpr")
@NodeChild("indexExpr")
public abstract class ArrayIndexingExprNode extends EasyScriptExprNode {
    @Specialization(guards = "arraysInteropLibrary.hasArrayElements(array)", limit = "3")
    protected Object readIntIndex(Object array, int index,
            @CachedLibrary("array") InteropLibrary arraysInteropLibrary) {
        try {
            return arraysInteropLibrary.readArrayElement(array, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    @Fallback
    protected Object readNonArrayOrNonIntIndex(@SuppressWarnings("unused") Object array,
            @SuppressWarnings("unused") Object index) {
        return Undefined.INSTANCE;
    }
}
