package com.endoflineblog.truffle.part_12.nodes.exprs.strings;

import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.FunctionObject;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ObjectPrototypePropertyReadCacheNode extends Node {
    public abstract Object executeObjectPrototypeReadProperty(
            Object object,
            TruffleString propertyName,
            JavaScriptObject objectPrototype,
            DynamicObjectLibrary objectLibrary);

    @Specialization(guards = {
            "object == cachedObject",
            "equals(propertyName, cachedPropertyName, equalNode)"
    })
    protected Object executeCached(
            @SuppressWarnings("unused") Object object,
            @SuppressWarnings("unused") TruffleString propertyName,
            @SuppressWarnings("unused") JavaScriptObject objectPrototype,
            @SuppressWarnings("unused") DynamicObjectLibrary objectLibrary,
            @SuppressWarnings("unused") @Cached TruffleString.EqualNode equalNode,
            @SuppressWarnings("unused") @Cached("object") Object cachedObject,
            @SuppressWarnings("unused") @Cached("propertyName") TruffleString cachedPropertyName,
            @SuppressWarnings("unused") @Cached ObjectPropertyValueNode objectPropertyValueNode,
            @Cached("objectPropertyValueNode.executeObjectPropertyValue(object, objectLibrary.getOrDefault(objectPrototype, propertyName, null))")
            Object cachedPropertyValue) {
        return cachedPropertyValue;
    }

    @Specialization(replaces = "executeCached")
    protected Object executeUncached(
            Object object,
            TruffleString propertyName,
            JavaScriptObject objectPrototype,
            DynamicObjectLibrary objectLibrary,
            @Cached ObjectPropertyValueNode objectPropertyValueNode) {
        Object propertyValue = objectLibrary.getOrDefault(objectPrototype, propertyName, null);
        return objectPropertyValueNode.executeObjectPropertyValue(object, propertyValue);
    }

    static abstract class ObjectPropertyValueNode extends Node {
        public abstract Object executeObjectPropertyValue(Object object, Object propertyValue);

        @Specialization
        protected FunctionObject whenValueIsMethod(Object object, FunctionObject propertyValue) {
            return propertyValue.withMethodTarget(object);
        }

        @Specialization(guards = "propertyValue == null")
        protected Undefined whenPropertyNotFound(
                @SuppressWarnings("unused") Object object,
                @SuppressWarnings("unused") Object propertyValue) {
            return Undefined.INSTANCE;
        }

        @Fallback
        protected Object whenValueIsNonMethod(
                @SuppressWarnings("unused") Object object,
                Object propertyValue) {
            return propertyValue;
        }
    }
}
