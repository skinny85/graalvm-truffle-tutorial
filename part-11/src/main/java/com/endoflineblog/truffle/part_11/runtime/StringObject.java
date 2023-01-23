package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public final class StringObject implements TruffleObject {
    private final String value;

    public StringObject(String value) {
        this.value = value;
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
    boolean isMemberReadable(String member) {
        return "length".equals(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        switch (member) {
            case "length": return this.value.length();
            default: throw UnknownIdentifierException.create(member);
        }
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new String[]{"length"});
    }
}
