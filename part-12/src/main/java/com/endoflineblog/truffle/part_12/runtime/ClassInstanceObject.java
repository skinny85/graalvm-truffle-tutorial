package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A {@link TruffleObject} that represents an instance of a user-defined class.
 * Instances of this class are created in the
 * {@link com.endoflineblog.truffle.part_12.nodes.exprs.objects.NewExprNode 'new' operator expression Node}.
 * It contains a pointer to the {@link ClassPrototypeObject prototype object of the class it belongs to},
 * and it delegates all member reads from the {@link InteropLibrary}
 * to that prototype, since, in this part of the series,
 * we only support instance methods of classes, not fields.
 */
@ExportLibrary(InteropLibrary.class)
public final class ClassInstanceObject implements TruffleObject {
    // this can't be private, because it's used in specialization guard expressions
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
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @CachedLibrary("this.classPrototypeObject") DynamicObjectLibrary dynamicObjectLibrary) {
        return new MemberNamesObject(dynamicObjectLibrary.getKeyArray(this.classPrototypeObject));
    }
}
