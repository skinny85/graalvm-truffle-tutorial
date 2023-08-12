package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class ObjectPropertyWriteNode extends EasyScriptNode {
    public abstract Object executePropertyWrite(Object object, Object property, Object propertyValue);

    @Specialization(limit = "1")
    protected Object writeTruffleStringProperty(JavaScriptObject object, TruffleString propertyName, Object propertyValue,
            @CachedLibrary("object") DynamicObjectLibrary dynamicObjectLibrary) {
        dynamicObjectLibrary.putWithFlags(object, propertyName, propertyValue, 0);
        return propertyValue;
    }

    @Specialization(limit = "1")
    protected Object writeObjectProperty(JavaScriptObject object, Object property, Object propertyValue,
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode,
            @CachedLibrary("object") DynamicObjectLibrary dynamicObjectLibrary) {
        return this.writeTruffleStringProperty(object, EasyScriptTruffleStrings.fromJavaString(
                        EasyScriptTruffleStrings.toString(property),
                        fromJavaStringNode),
                propertyValue, dynamicObjectLibrary);
    }

    @Specialization(guards = "interopLibrary.isNull(object)", limit = "1")
    protected Object writePropertyOfUndefined(@SuppressWarnings("unused") Object object,
            Object property, @SuppressWarnings("unused") Object propertyValue,
            @SuppressWarnings("unused") @CachedLibrary("object") InteropLibrary interopLibrary) {
        throw new EasyScriptException("Cannot set properties of undefined (setting '" + property + "')");
    }

    @Fallback
    protected Object writeNonObjectNonUndefinedProperty(@SuppressWarnings("unused") Object object,
            @SuppressWarnings("unused") Object property, Object propertyValue) {
        return propertyValue;
    }
}
