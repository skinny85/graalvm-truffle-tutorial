package com.endoflineblog.truffle.part_11.runtime;

import com.endoflineblog.truffle.part_11.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.root.BuiltInFuncRootNode;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public final class StringObject implements TruffleObject {
    private final String value;
    private final EasyScriptTruffleLanguage easyScriptTruffleLanguage;
    private final FunctionObject charAtMethod;

    public StringObject(String value, EasyScriptTruffleLanguage easyScriptTruffleLanguage) {
        this.value = value;
        this.easyScriptTruffleLanguage = easyScriptTruffleLanguage;

        var bodyExpr = CharAtMethodBodyExprNodeFactory.create(
                new ReadFunctionArgExprNode[]{new ReadFunctionArgExprNode(0)},
                this);
        BuiltInFuncRootNode charAtRootNode = new BuiltInFuncRootNode(easyScriptTruffleLanguage, bodyExpr);
        this.charAtMethod = new FunctionObject(charAtRootNode.getCallTarget(), 1);
    }

    @Override
    public String toString() {
        return this.value;
    }

    public StringObject charAt(int index) {
        return new StringObject(index >= 0 && index < this.value.length()
                    ? this.value.substring(index, index + 1)
                    : "",
                this.easyScriptTruffleLanguage);
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
        return this.value.length();
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < this.getArraySize();
    }

    @ExportMessage
    Object readArrayElement(long index) {
        int i = (int) index;
        return this.isArrayElementReadable(index)
                ? new StringObject(this.value.substring(i, i + 1), this.easyScriptTruffleLanguage)
                : Undefined.INSTANCE;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return "length".equals(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        switch (member) {
            case "length": return this.value.length();
            case "charAt": return this.charAtMethod;
            default: throw UnknownIdentifierException.create(member);
        }
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new String[]{"length"});
    }
}
