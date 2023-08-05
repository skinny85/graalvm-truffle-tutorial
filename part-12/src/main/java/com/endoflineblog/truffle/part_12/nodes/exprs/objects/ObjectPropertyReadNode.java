package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
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
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 * Similar to the class with the same name from part 10,
 * the only difference is we add a specialization for when the target of the property read is a
 * {@link TruffleString}, in which case we delegate to {@link ReadTruffleStringPropertyExprNode}.
 *
 * @see #readPropertyOfString
 */
public abstract class ObjectPropertyReadNode extends EasyScriptNode {
    public abstract Object executePropertyRead(Object target, Object property);

    /**
     * A specialization for reading a string property of a string,
     * in code like {@code "a"['length']}.
     * We delegate to {@link ReadTruffleStringPropertyExprNode},
     * but we first convert the property name to a Java string,
     * which is what {@link ReadTruffleStringPropertyExprNode} expects
     * (it's very likely it's a TruffleString).
     */
    @Specialization(limit = "1", guards = "propertyInteropLibrary.isString(propertyName)")
    protected Object readStringPropertyOfString(TruffleString target, Object propertyName,
            @CachedLibrary("propertyName") InteropLibrary propertyInteropLibrary,
            @Cached ReadTruffleStringPropertyExprNode readStringPropertyExprNode) {
        try {
            return readStringPropertyExprNode.executeReadTruffleStringProperty(target,
                    propertyInteropLibrary.asString(propertyName));
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    /**
     * The specialization for reading a property of a {@link TruffleString}.
     * Simply delegates to {@link ReadTruffleStringPropertyExprNode}.
     */
    @Specialization
    protected Object readPropertyOfString(TruffleString truffleString, Object property,
            @Cached ReadTruffleStringPropertyExprNode readStringPropertyExprNode) {
        return readStringPropertyExprNode.executeReadTruffleStringProperty(
                truffleString, property);
    }

    @Specialization(limit = "1")
    protected Object readPropertyOfJavaScriptObject(JavaScriptObject object, TruffleString propertyName,
            @CachedLibrary("object") DynamicObjectLibrary dynamicObjectLibrary) {
        return dynamicObjectLibrary.getOrDefault(object, propertyName, null);
    }

    @Specialization(guards = {
            "objectInteropLibrary.hasMembers(object)",
            "propertyInteropLibrary.isString(property)"
    }, limit = "1")
    protected Object readPropertyOfInteropObject(Object object, Object property,
            @CachedLibrary("object") InteropLibrary objectInteropLibrary,
            @CachedLibrary("property") InteropLibrary propertyInteropLibrary) {
        try {
            return objectInteropLibrary.readMember(object, propertyInteropLibrary.asString(property));
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
    @Specialization(guards = "interopLibrary.isNull(target)", limit = "1")
    protected Object readPropertyOfUndefined(@SuppressWarnings("unused") Object target, Object property,
            @CachedLibrary("target") @SuppressWarnings("unused") InteropLibrary interopLibrary) {
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + property + "')");
    }

    /**
     * Accessing a property of anything that is not {@code undefined}
     * but doesn't have any members returns simply {@code undefined}
     * in JavaScript.
     */
    @Fallback
    protected Object readPropertyOfNonUndefinedWithoutMembers(@SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") Object property) {
        return Undefined.INSTANCE;
    }
}
