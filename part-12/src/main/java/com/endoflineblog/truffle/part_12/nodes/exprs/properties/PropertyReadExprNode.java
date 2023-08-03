package com.endoflineblog.truffle.part_12.nodes.exprs.properties;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
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
@NodeChild("target")
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    private final String javaStringPropertyName;
    private final TruffleString truffleStringPropertyName;

    protected PropertyReadExprNode(String propertyName) {
        this.javaStringPropertyName = propertyName;
        this.truffleStringPropertyName = EasyScriptTruffleStrings.fromJavaString(propertyName);
    }

    /**
     * The specialization for reading a property of a {@link TruffleString}.
     * Simply delegates to {@link ReadTruffleStringPropertyExprNode}.
     */
    @Specialization
    protected Object readPropertyOfString(TruffleString target,
            @Cached ReadTruffleStringPropertyExprNode readStringPropertyExprNode) {
        return readStringPropertyExprNode.executeReadTruffleStringProperty(
                target, this.javaStringPropertyName);
    }

    @Specialization(limit = "1")
    protected Object readPropertyOfJavaScriptObject(JavaScriptObject target,
            @CachedLibrary("target") DynamicObjectLibrary dynamicObjectLibrary) {
        return dynamicObjectLibrary.getOrDefault(target, this.truffleStringPropertyName, null);
    }

    @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "1")
    protected Object readProperty(Object target,
            @CachedLibrary("target") InteropLibrary interopLibrary) {
        try {
            return interopLibrary.readMember(target, this.javaStringPropertyName);
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
    protected Object readPropertyOfUndefined(@SuppressWarnings("unused") Object target,
            @CachedLibrary("target") @SuppressWarnings("unused") InteropLibrary interopLibrary) {
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + this.javaStringPropertyName + "')");
    }

    /**
     * Accessing a property of anything that is not {@code undefined}
     * but doesn't have any members returns simply {@code undefined}
     * in JavaScript.
     */
    @Fallback
    protected Object readPropertyOfNonUndefinedWithoutMembers(@SuppressWarnings("unused") Object target) {
        return Undefined.INSTANCE;
    }
}
