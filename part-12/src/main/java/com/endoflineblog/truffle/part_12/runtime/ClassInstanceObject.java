package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

@ExportLibrary(InteropLibrary.class)
public final class ClassInstanceObject implements TruffleObject {
    final ClassPrototypeObject classPrototypeObject;

    public ClassInstanceObject(ClassPrototypeObject classPrototypeObject) {
        this.classPrototypeObject = classPrototypeObject;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary dynamicObjectLibrary) {
        return dynamicObjectLibrary.containsKey(this.classPrototypeObject, member);
    }

    @ExportMessage
    Object readMember(String member, @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary dynamicObjectLibrary)
            throws UnknownIdentifierException {
        Object value = dynamicObjectLibrary.getOrDefault(this.classPrototypeObject, member, null);
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new Object[]{});
    }
}
