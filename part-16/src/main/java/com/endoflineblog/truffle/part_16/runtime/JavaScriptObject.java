package com.endoflineblog.truffle.part_16.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link DynamicObject} that is the base class of all objects in EasyScript,
 * including user-defined class instances, built-in objects like arrays and functions,
 * and class prototypes.
 * Identical to the class with the same name from part 15.
 */
@ExportLibrary(InteropLibrary.class)
public class JavaScriptObject extends DynamicObject {
    public final DynamicObject prototype;

    public JavaScriptObject(Shape shape, DynamicObject prototype) {
        super(shape);

        this.prototype = prototype;
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
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary) {
        return thisObjectLibrary.containsKey(this, member) ||
                prototypeInteropLibrary.isMemberReadable(this.prototype, member);
    }

    @ExportMessage
    Object readMember(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary)
            throws UnknownIdentifierException, UnsupportedMessageException {
        Object value = thisObjectLibrary.getOrDefault(this, member, null);
        if (value == null) {
            return prototypeInteropLibrary.readMember(this.prototype, member);
        }
        return value;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        return new MemberNamesObject(thisObjectLibrary.getKeyArray(this));
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary) {
        return this.isMemberReadable(member, thisObjectLibrary, prototypeInteropLibrary);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary) {
        return !this.isMemberModifiable(member, thisObjectLibrary, prototypeInteropLibrary);
    }

    @ExportMessage
    void writeMember(String member, Object value,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        thisObjectLibrary.put(this, member, value);
    }
}
