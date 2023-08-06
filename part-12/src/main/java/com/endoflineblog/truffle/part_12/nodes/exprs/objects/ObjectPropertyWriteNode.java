package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class ObjectPropertyWriteNode extends EasyScriptNode {
    public abstract void executePropertyWrite(JavaScriptObject object, Object property, Object propertyValue);

    @Specialization(limit = "1")
    protected void writeTruffleStringProperty(JavaScriptObject object, TruffleString propertyName, Object propertyValue,
            @CachedLibrary("object") DynamicObjectLibrary dynamicObjectLibrary) {
        dynamicObjectLibrary.putWithFlags(object, propertyName, propertyValue, 0);
    }

    @Fallback
    protected void writeObjectProperty(JavaScriptObject object, Object property, Object propertyValue,
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode,
            @CachedLibrary(limit = "1") DynamicObjectLibrary dynamicObjectLibrary) {
        this.writeTruffleStringProperty(object, EasyScriptTruffleStrings.fromJavaString(
                        EasyScriptTruffleStrings.toString(property),
                        fromJavaStringNode),
                propertyValue, dynamicObjectLibrary);
    }
}
