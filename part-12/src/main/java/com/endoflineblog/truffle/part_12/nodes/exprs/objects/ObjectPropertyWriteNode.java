package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class ObjectPropertyWriteNode extends EasyScriptNode {
    public abstract void executePropertyWrite(JavaScriptObject object, Object key, Object value);

    @Specialization(limit = "1")
    protected void writeTruffleStringKey(JavaScriptObject object, TruffleString key, Object value,
            @CachedLibrary("object") DynamicObjectLibrary dynamicObjectLibrary) {
        dynamicObjectLibrary.putWithFlags(object, key, value, 0);
    }

    @Specialization(limit = "1")
    protected void writeObjectKey(JavaScriptObject object, Object key, Object value,
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode,
            @CachedLibrary("object") DynamicObjectLibrary dynamicObjectLibrary) {
        this.writeTruffleStringKey(object, EasyScriptTruffleStrings.fromJavaString(
                        EasyScriptTruffleStrings.toString(key),
                        fromJavaStringNode),
                value, dynamicObjectLibrary);
    }
}
