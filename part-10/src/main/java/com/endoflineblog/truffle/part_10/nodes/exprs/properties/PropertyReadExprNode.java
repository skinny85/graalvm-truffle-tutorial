package com.endoflineblog.truffle.part_10.nodes.exprs.properties;

import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.runtime.Undefined;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

@NodeChild("target")
@NodeField(name = "propertyName", type = String.class)
public abstract class PropertyReadExprNode extends EasyScriptExprNode {
    protected abstract String getPropertyName();

    @Specialization(guards = "interopLibrary.hasMembers(target)", limit = "3")
    public Object readProperty(Object target,
            @CachedLibrary("target") InteropLibrary interopLibrary) {
        try {
            return interopLibrary.readMember(target, this.getPropertyName());
        } catch (UnknownIdentifierException e) {
            return Undefined.INSTANCE;
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }
}