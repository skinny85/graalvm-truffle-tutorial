package com.endoflineblog.truffle.part_15.nodes.exprs.properties;

import com.endoflineblog.truffle.part_15.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_15.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_15.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_15.nodes.exprs.arrays.ArrayIndexReadExprNode;
import com.endoflineblog.truffle.part_15.nodes.exprs.strings.ReadTruffleStringPropertyNode;
import com.endoflineblog.truffle.part_15.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_15.runtime.ErrorJavaScriptObject;
import com.endoflineblog.truffle.part_15.runtime.ObjectPrototype;
import com.endoflineblog.truffle.part_15.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * A Node for reading a property of a JavaScript object.
 * Used by {@link PropertyReadExprNode} and {@link ArrayIndexReadExprNode}.
 * Very similar to the class with the same name from part 14,
 * the only difference is that reading a property of {@code undefined}
 * now throws an {@link EasyScriptException} with an instance of
 * {@link ErrorJavaScriptObject} that represents a {@code TypeError}.
 */
public abstract class CommonReadPropertyNode extends EasyScriptNode {
    public abstract Object executeReadProperty(Object target, Object property);

    /**
     * The specialization for reading a property of a {@link TruffleString}.
     * Simply delegates to {@link ReadTruffleStringPropertyNode}.
     */
    @Specialization
    protected Object readPropertyOfString(TruffleString target, Object property,
            @Cached ReadTruffleStringPropertyNode readStringPropertyNode) {
        return readStringPropertyNode.executeReadTruffleStringProperty(
                target, property);
    }

    @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "2")
    protected Object readProperty(Object target, String propertyName,
            @CachedLibrary("target") InteropLibrary interopLibrary) {
        try {
            return interopLibrary.readMember(target, propertyName);
        } catch (UnknownIdentifierException e) {
            return Undefined.INSTANCE;
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * Reading any property of {@code undefined}
     * results in an error in JavaScript.
     */
    @Specialization(guards = "interopLibrary.isNull(target)", limit = "2")
    protected Object readPropertyOfUndefined(
            @SuppressWarnings("unused") Object target,
            Object property,
            @CachedLibrary("target") @SuppressWarnings("unused") InteropLibrary interopLibrary,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary,
            @Cached("currentLanguageContext().shapesAndPrototypes") ShapesAndPrototypes shapesAndPrototypes) {
        var typeError = new ErrorJavaScriptObject(
                "TypeError",
                "Cannot read properties of undefined (reading '" + property + "')",
                dynamicObjectLibrary,
                shapesAndPrototypes.rootShape,
                shapesAndPrototypes.errorPrototypes.typeErrorPrototype);
        throw new EasyScriptException(typeError, this);
    }

    /**
     * Accessing a property of anything that is not {@code undefined}
     * but doesn't have any members reads from the Object prototype.
     */
    @Fallback
    protected Object readPropertyOfNonUndefinedWithoutMembers(@SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") Object property,
            @Cached("currentLanguageContext().shapesAndPrototypes.objectPrototype") ObjectPrototype objectPrototype,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
        return dynamicObjectLibrary.getOrDefault(objectPrototype,
                EasyScriptTruffleStrings.toString(property), Undefined.INSTANCE);
    }
}
