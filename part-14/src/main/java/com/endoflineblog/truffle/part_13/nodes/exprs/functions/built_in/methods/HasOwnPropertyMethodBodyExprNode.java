package com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.strings.ReadTruffleStringPropertyNode;
import com.endoflineblog.truffle.part_13.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class HasOwnPropertyMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization(limit = "2")
    protected boolean hasOwnPropertyDynamicObject(
            DynamicObject self, Object property,
            @CachedLibrary("self") DynamicObjectLibrary dynamicObjectLibrary) {
        return dynamicObjectLibrary.containsKey(self, EasyScriptTruffleStrings.toString(property));
    }

    @Specialization
    protected boolean hasOwnPropertyTruffleString(
            @SuppressWarnings("unused") TruffleString self,
            Object property) {
        // strings only have the 'length' property
        return ReadTruffleStringPropertyNode.LENGTH_PROP.equals(EasyScriptTruffleStrings.toString(property));
    }

    @Fallback
    protected boolean hasOwnPropertyPrimitive(
            @SuppressWarnings("unused") Object self,
            @SuppressWarnings("unused") Object property) {
        // primitives don't own any properties
        return false;
    }
}
