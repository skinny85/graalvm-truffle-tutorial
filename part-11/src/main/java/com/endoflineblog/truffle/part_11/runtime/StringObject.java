package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public final class StringObject implements TruffleObject {
    private final String value;
    private final FunctionObject charAtMethod;
    private final FunctionObject substringMethod;
    private final StringPrototype stringPrototype;

    public StringObject(String value, StringPrototype stringPrototype) {
        this.value = value;
        this.charAtMethod = new FunctionObject(stringPrototype.charAtMethod, 2, this);
        this.substringMethod = new FunctionObject(stringPrototype.substringMethod, 3, this);
        this.stringPrototype = stringPrototype;
    }

    @TruffleBoundary
    public StringObject charAt(int index) {
        return new StringObject(index >= 0 && index < this.value.length()
                    ? this.value.substring(index, index + 1)
                    : "",
                this.stringPrototype);
    }

    @TruffleBoundary
    public StringObject substring(int start) {
        return new StringObject(
                this.value.substring(start),
                this.stringPrototype);
    }

    @TruffleBoundary
    public StringObject substring(int start, int end) {
        return new StringObject(
                this.value.substring(start, end),
                this.stringPrototype);
    }

    @Override
    public String toString() {
        return this.value;
    }

    @ExportMessage
    boolean isString() {
        return true;
    }

    @ExportMessage
    String asString() {
        return this.value;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return this.length();
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < this.getArraySize();
    }

    @ExportMessage
    Object readArrayElement(long index) {
        int i = (int) index;
        return this.isArrayElementReadable(index)
                ? this.substring(i, i + 1)
                : Undefined.INSTANCE;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return "length".equals(member) || "charAt".equals(member) ||
                "substring".equals(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        switch (member) {
            case "length": return this.length();
            case "charAt": return this.charAtMethod;
            case "substring": return this.substringMethod;
            default: throw UnknownIdentifierException.create(member);
        }
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new String[]{"length", "charAt", "substring"});
    }

    @TruffleBoundary
    private int length() {
        return this.value.length();
    }
}
