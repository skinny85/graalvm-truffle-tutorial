package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
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
public class JavaScriptObject extends DynamicObject {
    // this can't be private, because it's used in specialization guard expressions
    final ClassPrototypeObject classPrototypeObject;

    public JavaScriptObject(Shape shape, ClassPrototypeObject classPrototypeObject) {
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
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @CachedLibrary("this") DynamicObjectLibrary instanceObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary prototypeObjectLibrary) {
        return instanceObjectLibrary.containsKey(this, member) ||
                prototypeObjectLibrary.containsKey(this.classPrototypeObject, member);
    }

    @ExportMessage
    Object readMember(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary prototypeObjectLibrary)
            throws UnknownIdentifierException {
        // since ClassInstanceObject is mutable, we need to check it first, before the prototype
        Object value = thisObjectLibrary.getOrDefault(this, member, null);
        if (value == null) {
            value = prototypeObjectLibrary.getOrDefault(this.classPrototypeObject, member, null);
        }
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @CachedLibrary("this") DynamicObjectLibrary dynamicObjectLibrary) {
        return new MemberNamesObject(dynamicObjectLibrary.getKeyArray(this));
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @CachedLibrary("this") DynamicObjectLibrary instanceObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary prototypeObjectLibrary) {
        return this.isMemberReadable(member, instanceObjectLibrary, prototypeObjectLibrary);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @CachedLibrary("this") DynamicObjectLibrary instanceObjectLibrary,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary dynamicObjectLibrary) {
        return !this.isMemberModifiable(member, instanceObjectLibrary, dynamicObjectLibrary);
    }

    @ExportMessage
    void writeMember(String member, Object value,
            @CachedLibrary("this") DynamicObjectLibrary dynamicObjectLibrary) {
        dynamicObjectLibrary.put(this, member, value);
    }
}
