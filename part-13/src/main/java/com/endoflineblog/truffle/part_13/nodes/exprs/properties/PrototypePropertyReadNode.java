package com.endoflineblog.truffle.part_13.nodes.exprs.properties;

import com.endoflineblog.truffle.part_13.runtime.ClassInstanceObject;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_13.runtime.FunctionObject;
import com.endoflineblog.truffle.part_13.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class PrototypePropertyReadNode extends Node {
    public abstract Object executePropertyRead(
            Object target,
            Object property,
            ClassPrototypeObject targetPrototype);

    @Specialization(guards = {
            "target == cachedTarget",
            "property.equals(cachedProperty)"
    }, limit = "2")
    protected Object executeTruffleStringCached(
            @SuppressWarnings("unused") TruffleString target,
            @SuppressWarnings("unused") Object property,
            @SuppressWarnings("unused") ClassPrototypeObject targetPrototype,
            @SuppressWarnings("unused") @Cached("target") TruffleString cachedTarget,
            @SuppressWarnings("unused") @Cached("property") Object cachedProperty,
            @SuppressWarnings("unused") @Cached PrototypeExtractPropertyNode prototypeExtractPropertyNode,
            @Cached("prototypeExtractPropertyNode.executePrototypePropertyExtraction(target, property, targetPrototype)")
            Object cachedPropertyValue) {
        return cachedPropertyValue;
    }

    @Specialization(guards = {
            "target == cachedTarget",
            "property.equals(cachedProperty)"
    }, assumptions = "cachedTarget.getObjectWasWrittenToAssumption()", limit = "2")
    protected Object executeClassInstanceCached(
            @SuppressWarnings("unused") ClassInstanceObject target,
            @SuppressWarnings("unused") Object property,
            @SuppressWarnings("unused") ClassPrototypeObject targetPrototype,
            @SuppressWarnings("unused") @Cached("target") ClassInstanceObject cachedTarget,
            @SuppressWarnings("unused") @Cached("property") Object cachedProperty,
            @SuppressWarnings("unused") @Cached PrototypeExtractPropertyNode prototypeExtractPropertyNode,
            @Cached("prototypeExtractPropertyNode.executePrototypePropertyExtraction(target, property, targetPrototype)")
            Object cachedPropertyValue) {
        return cachedPropertyValue;
    }

    @Specialization(replaces = {"executeClassInstanceCached", "executeTruffleStringCached"})
    protected Object executeUncached(
            Object target,
            Object property,
            ClassPrototypeObject targetPrototype,
            @Cached PrototypeExtractPropertyNode prototypeExtractPropertyNode) {
        return prototypeExtractPropertyNode.executePrototypePropertyExtraction(target, property, targetPrototype);
    }

    protected static abstract class PrototypeExtractPropertyNode extends Node {
        public abstract Object executePrototypePropertyExtraction(Object target, Object property,
                ClassPrototypeObject targetPrototype);

        @Specialization(limit = "1")
        protected Object extractFromTruffleString(TruffleString target, Object property,
                ClassPrototypeObject targetPrototype,
                @CachedLibrary("targetPrototype") DynamicObjectLibrary targetPrototypeObjectLibrary,
                @Cached PrototypePropertyValueNode prototypePropertyValueNode) {
            // strings are immutable, which means we know the read should be from the prototype
            // (ReadTruffleStringPropertyNode handles other properties, like 'length')
            return prototypePropertyValueNode.executePrototypePropertyValue(target,
                    targetPrototypeObjectLibrary.getOrDefault(targetPrototype, property, null));
        }

        @Specialization(limit = "1")
        protected Object extractFromClassInstanceOrPrototype(ClassInstanceObject target, Object property,
                ClassPrototypeObject targetPrototype,
                @CachedLibrary("target") DynamicObjectLibrary targetObjectLibrary,
                @CachedLibrary("targetPrototype") DynamicObjectLibrary targetPrototypeObjectLibrary,
                @Cached PrototypePropertyValueNode prototypePropertyValueNode) {
            // since ClassInstanceObject is mutable, we need to check it first,
            // before the prototype
            Object value = targetObjectLibrary.getOrDefault(target, property, null);
            if (value == null) {
                value = targetPrototypeObjectLibrary.getOrDefault(targetPrototype, property, null);
            }
            return prototypePropertyValueNode.executePrototypePropertyValue(target, value);
        }
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
