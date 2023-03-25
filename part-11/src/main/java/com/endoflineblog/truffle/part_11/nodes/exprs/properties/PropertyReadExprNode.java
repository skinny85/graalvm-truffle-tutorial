package com.endoflineblog.truffle.part_11.nodes.exprs.properties;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node for reading properties of objects.
 * Used to implement the {@code length} property of arrays.
 * Uses the {@link InteropLibrary#readMember} method in its implementation.
 */
@NodeChild("target")
@NodeField(name = "propertyName", type = String.class)
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    protected abstract String getPropertyName();

    @Specialization
    protected Object readPropertyFromTruffleString(TruffleString target,
            @Cached ReadTruffleStringPropertyExprNode readStringPropertyExprNode) {
        return readStringPropertyExprNode.executeReadTruffleStringProperty(
                target, this.getPropertyName());
    }

    @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "1")
    protected Object readProperty(Object target,
            @CachedLibrary("target") InteropLibrary interopLibrary) {
        try {
            return interopLibrary.readMember(target, this.getPropertyName());
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
        throw new EasyScriptException("Cannot read properties of undefined (reading '" + this.getPropertyName() + "')");
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
