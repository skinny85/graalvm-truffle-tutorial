package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link TruffleObject} that is the base class of all objects in EasyScript,
 * including user-defined class instances, and built-in objects like arrays and functions.
 * Very similar to {@code ClassInstanceObject} from part 12,
 * but with added support for writing properties,
 * using the {@link InteropLibrary} interface,
 * that is invoked in {@link com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonWritePropertyNode}.
 */
@ExportLibrary(InteropLibrary.class)
public class JavaScriptObject extends InteropDynamicObject {
    // this can't be private, because it's used in specialization guard expressions
    final AbstractClassPrototypeObject classPrototypeObject;

    public JavaScriptObject(Shape shape, AbstractClassPrototypeObject classPrototypeObject) {
        super(shape);

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
    boolean isMemberReadable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary prototypeObjectLibrary) {
        return thisObjectLibrary.containsKey(this, member) ||
                prototypeObjectLibrary.containsKey(this.classPrototypeObject, member);
    }

    @ExportMessage
    Object readMember(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") InteropLibrary prototypeInteropLibrary)
            throws UnknownIdentifierException, UnsupportedMessageException {
        // since ClassInstanceObject is mutable, we need to check it first, before the prototype
        Object value = thisObjectLibrary.getOrDefault(this, member, null);
        if (value == null) {
            return prototypeInteropLibrary.readMember(this.classPrototypeObject, member);
        }
        return value;
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary prototypeObjectLibrary) {
        return this.isMemberReadable(member, thisObjectLibrary, prototypeObjectLibrary);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary prototypeObjectLibrary) {
        return !this.isMemberModifiable(member, thisObjectLibrary, prototypeObjectLibrary);
    }

    @ExportMessage
    void writeMember(String member, Object value,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        thisObjectLibrary.put(this, member, value);
    }
}
