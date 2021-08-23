package com.endoflineblog.truffle.part_05.runtime;

import com.endoflineblog.truffle.part_05.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ExportLibrary(InteropLibrary.class)
public final class GlobalScopeObject implements TruffleObject {
    private final Map<String, Object> values = new HashMap<>();

    public boolean newValue(String name, Object value) {
        Object existingValue = values.putIfAbsent(name, value);
        return existingValue == null;
    }

    public boolean updateValue(String name, Object value) {
        Object existingValue = values.computeIfPresent(name, (k, v) -> value);
        return existingValue != null;
    }

    public Object getValue(String name) {
        return this.values.get(name);
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return this.values.containsKey(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        Object value = this.values.get(member);
        if (null == value) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new GlobalVariableNamesObject(this.values.keySet());
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return "global";
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<?>> getLanguage() {
        return EasyScriptTruffleLanguage.class;
    }
}

@ExportLibrary(InteropLibrary.class)
class GlobalVariableNamesObject implements TruffleObject {
    private final String[] names;

    GlobalVariableNamesObject(Set<String> names) {
        this.names = names.toArray(new String[]{});
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return names.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < names.length;
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (!isArrayElementReadable(index)) {
            throw InvalidArrayIndexException.create(index);
        }
        return names[(int) index];
    }
}
