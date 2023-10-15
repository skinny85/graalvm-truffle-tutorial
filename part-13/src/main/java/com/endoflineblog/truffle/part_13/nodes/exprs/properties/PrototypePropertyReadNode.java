package com.endoflineblog.truffle.part_13.nodes.exprs.properties;

import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_13.runtime.FunctionObject;
import com.endoflineblog.truffle.part_13.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public abstract class PrototypePropertyReadNode extends Node {
    public abstract Object executePropertyRead(
            Object target,
            Object property,
            ClassPrototypeObject targetPrototype,
            DynamicObjectLibrary objectLibrary);

    @Specialization(guards = {
            "target == cachedTarget",
            "property.equals(cachedProperty)"
    })
    protected Object executeCached(
            @SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") Object property,
            @SuppressWarnings("unused") ClassPrototypeObject targetPrototype,
            @SuppressWarnings("unused") DynamicObjectLibrary objectLibrary,
            @SuppressWarnings("unused") @Cached("target") Object cachedTarget,
            @SuppressWarnings("unused") @Cached("property") Object cachedProperty,
            @SuppressWarnings("unused") @Cached PrototypePropertyValueNode prototypePropertyValueNode,
            @Cached("prototypePropertyValueNode.executePrototypePropertyValue(target, objectLibrary.getOrDefault(targetPrototype, property, null))")
            Object cachedPropertyValue) {
        return cachedPropertyValue;
    }

    @Specialization(replaces = "executeCached")
    protected Object executeUncached(
            Object target,
            Object property,
            ClassPrototypeObject targetPrototype,
            DynamicObjectLibrary objectLibrary,
            @Cached PrototypePropertyValueNode prototypePropertyValueNode) {
        Object propertyValue = objectLibrary.getOrDefault(targetPrototype, property, null);
        return prototypePropertyValueNode.executePrototypePropertyValue(target, propertyValue);
    }

    protected static abstract class PrototypePropertyValueNode extends Node {
        public abstract Object executePrototypePropertyValue(Object object, Object propertyValue);

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
