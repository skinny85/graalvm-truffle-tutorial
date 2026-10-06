package com.endoflineblog.truffle.part_14.nodes.exprs.properties;

import com.endoflineblog.truffle.part_14.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_14.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_14.nodes.exprs.strings.ReadTruffleStringPropertyNode;
import com.endoflineblog.truffle.part_14.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_14.runtime.ObjectPrototype;
import com.endoflineblog.truffle.part_14.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * A Node for reading a property of a JavaScript object.
 * Used by {@link PropertyReadExprNode} and {@link com.endoflineblog.truffle.part_14.nodes.exprs.arrays.ArrayIndexReadExprNode}.
 * Very similar to the class with the same name from part 13,
 * the only difference is a change in the implementation of the
 * {@link #readPropertyOfNonUndefinedWithoutMembers} method
 * to search for the property in the prototype of {@code Object},
 * in code like {@code true.hasOwnProperty('x')}.
 */
@GenerateInline(true)
public abstract class CommonReadPropertyNode extends EasyScriptNode {
    public abstract Object executeReadProperty(Node node, Object target, Object property);

    /**
     * The specialization for reading a property of a {@link TruffleString}.
     * Simply delegates to {@link ReadTruffleStringPropertyNode}.
     */
    @Specialization
    protected static Object readPropertyOfString(Node node, TruffleString target, Object property,
            @Cached ReadTruffleStringPropertyNode readStringPropertyNode) {
        return readStringPropertyNode.executeReadTruffleStringProperty(
                node, target, property);
    }

    @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "2")
    protected static Object readProperty(Node node, Object target, String propertyName,
            @CachedLibrary("target") InteropLibrary interopLibrary) {
        try {
            return interopLibrary.readMember(target, propertyName);
        } catch (UnknownIdentifierException e) {
            return Undefined.INSTANCE;
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(node, e.getMessage());
        }
    }

    /**
     * Reading any property of {@code undefined}
     * results in an error in JavaScript.
     */
    @Specialization(guards = "interopLibrary.isNull(target)", limit = "2")
    protected static Object readPropertyOfUndefined(@SuppressWarnings("unused") Object target, Object property,
            @CachedLibrary("target") @SuppressWarnings("unused") InteropLibrary interopLibrary) {
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + property + "')");
    }

    /**
     * Accessing a property of anything that is not {@code undefined}
     * but doesn't have any members reads from the Object prototype.
     */
    @Fallback
    protected static Object readPropertyOfNonUndefinedWithoutMembers(@SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") Object property,
            @Cached("currentLanguageContext().shapesAndPrototypes.objectPrototype") ObjectPrototype objectPrototype,
            @CachedLibrary("objectPrototype") DynamicObjectLibrary dynamicObjectLibrary) {
        return dynamicObjectLibrary.getOrDefault(objectPrototype,
                EasyScriptTruffleStrings.toString(property), Undefined.INSTANCE);
    }
}
