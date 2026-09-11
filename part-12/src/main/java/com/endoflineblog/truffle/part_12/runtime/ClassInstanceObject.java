package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;

@ExportLibrary(InteropLibrary.class)
public final class ClassInstanceObject implements TruffleObject {
    final ClassPrototypeObject classPrototypeObject;

    public ClassInstanceObject(ClassPrototypeObject classPrototypeObject) {
        this.classPrototypeObject = classPrototypeObject;
    }

    @Override
    public String toString() {
        return "[object Object]";
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.toString();
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @Cached DynamicObject.ContainsKeyNode containsKeyNode) {
        return containsKeyNode.execute(this.classPrototypeObject, member);
    }

    @ExportMessage
    Object readMember(String member,
            @Cached DynamicObject.GetNode getNode)
            throws UnknownIdentifierException {
        Object value = getNode.execute(this.classPrototypeObject, member, null);
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @Cached DynamicObject.GetKeyArrayNode getKeyArrayNode) {
        return new MemberNamesObject(getKeyArrayNode.execute(this.classPrototypeObject));
    }
}
