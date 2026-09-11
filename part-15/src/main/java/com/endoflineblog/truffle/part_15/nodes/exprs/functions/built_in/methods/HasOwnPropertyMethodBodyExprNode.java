package com.endoflineblog.truffle.part_15.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_15.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_15.nodes.exprs.strings.ReadTruffleStringPropertyNode;
import com.endoflineblog.truffle.part_15.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * An expression Node that represents the implementation of the built-in
 * {@code hasOwnProperty()} method of {@code Object}.
 * Identical to the class with the same name from part 16.
 */
public abstract class HasOwnPropertyMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    /**
     * The specialization for calling {@code hasOwnProperty()}
     * on an object.
     */
    @Specialization(guards = "property == cachedProperty", limit = "3")
    protected boolean hasOwnPropertyDynamicObject(
            DynamicObject self, Object property,
            @Cached("property") Object cachedProperty,
            @Cached @Shared("containsKey") DynamicObject.ContainsKeyNode containsKeyNode) {
        return containsKeyNode.execute(self, EasyScriptTruffleStrings.toString(cachedProperty));
    }

    @Specialization(replaces = "hasOwnPropertyDynamicObject")
    protected boolean hasOwnPropertyDynamicObjectGeneric(
            DynamicObject self, Object property,
            @Cached @Shared("containsKey") DynamicObject.ContainsKeyNode containsKeyNode) {
        return containsKeyNode.execute(self, EasyScriptTruffleStrings.toString(property));
    }

    /**
     * The specialization for calling {@code hasOwnProperty()}
     * on a string.
     */
    @Specialization
    protected boolean hasOwnPropertyTruffleString(
            @SuppressWarnings("unused") TruffleString self,
            Object property) {
        // strings only have the 'length' property
        return ReadTruffleStringPropertyNode.LENGTH_PROP.equals(EasyScriptTruffleStrings.toString(property));
    }

    /**
     * The specialization for calling {@code hasOwnProperty()}
     * on a primitive, like a number or boolean.
     */
    @Fallback
    protected boolean hasOwnPropertyPrimitive(
            @SuppressWarnings("unused") Object self,
            @SuppressWarnings("unused") Object property) {
        // primitives don't own any properties
        return false;
    }
}
