package com.endoflineblog.truffle.part_09.runtime;

import com.endoflineblog.truffle.part_09.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_09.common.DeclarationKind;
import com.endoflineblog.truffle.part_09.exceptions.EasyScriptException;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the Truffle interop object that represents the global-level scope
 * that contains all global variables.
 * Basically identical to the class with the same name from part 6,
 * the only differences being that we allow overriding members
 * (as functions can be overridden in JavaScript),
 * and that we have a "magical" default value for 'const' and 'let'
 * variables that throws if it's read before those variables are initialized.
 */
@ExportLibrary(InteropLibrary.class)
public final class GlobalScopeObject implements TruffleObject {
    private static final Object DUMMY = new Object() {
        @Override
        public String toString() {
            return "Dummy";
        }
    };

    private final Map<String, Object> variables = new HashMap<>();
    private final Set<String> constants = new HashSet<>();

    public boolean newVariable(String name, DeclarationKind declarationKind) {
        // We allow overwriting variables, because some things
        // (like functions) can be overwritten.
        // If it shouldn't be allowed, the caller of this method can check the return value,
        // and react appropriately (most likely, by throwing).
        Object existingValue = this.variables.put(name, declarationKind == DeclarationKind.VAR
                // the default value for 'var' is 'undefined'
                ? Undefined.INSTANCE
                // for 'const' and 'let', we write a "dummy" value that we treat specially
                : DUMMY);
        if (declarationKind == DeclarationKind.CONST) {
            this.constants.add(name);
        }
        return existingValue == null;
    }

    public void newFunction(String name, FunctionObject func) {
        // we allow overwriting functions
        this.variables.put(name, func);
    }

    public boolean updateVariable(String name, Object value) {
        Object existingValue = this.variables.put(name, value);
        if (existingValue == DUMMY) {
            // the first assignment to a constant is fine
            return true;
        }
        if (this.constants.contains(name)) {
            throw new com.endoflineblog.truffle.part_09.exceptions.EasyScriptException("Assignment to constant variable '" + name + "'");
        }
        return existingValue != null;
    }

    public Object getVariable(String name) {
        Object ret = this.variables.get(name);
        if (ret == DUMMY) {
            throw new EasyScriptException("Cannot access '" + name + "' before initialization");
        }
        return ret;
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new GlobalVariableNamesObject(this.variables.keySet());
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return this.variables.containsKey(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        Object value = this.variables.get(member);
        if (null == value) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
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

/**
 * The class that implements the collection of member names of the global scope.
 * Used in the {@link GlobalScopeObject#getMembers} method.
 * Identical to the class with the same name from part 6.
 */
@ExportLibrary(InteropLibrary.class)
final class GlobalVariableNamesObject implements TruffleObject {
    private final List<String> names;

    GlobalVariableNamesObject(Set<String> names) {
        this.names = new ArrayList<>(names);
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return this.names.size();
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < this.names.size();
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (!this.isArrayElementReadable(index)) {
            throw InvalidArrayIndexException.create(index);
        }
        return this.names.get((int) index);
    }
}
