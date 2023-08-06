package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.strings.TruffleString;

@ExportLibrary(InteropLibrary.class)
public final class JavaScriptObject extends DynamicObject {
    public JavaScriptObject(Shape shape) {
        super(shape);
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @Cached @Shared("fromJavaStringNode") TruffleString.FromJavaStringNode fromJavaStringNode,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.containsKey(this, EasyScriptTruffleStrings.fromJavaString(member, fromJavaStringNode));
    }

    @ExportMessage
    Object readMember(String member,
            @Cached @Shared("fromJavaStringNode") TruffleString.FromJavaStringNode fromJavaStringNode,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) throws UnknownIdentifierException {
        Object ret = objectLibrary.getOrDefault(this, EasyScriptTruffleStrings.fromJavaString(member, fromJavaStringNode), null);
        if (ret == null) {
            throw UnknownIdentifierException.create(member);
        }
        return ret;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return new MemberNamesObject(objectLibrary.getKeyArray(this));
    }
}
