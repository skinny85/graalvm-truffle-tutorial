package com.endoflineblog.truffle.part_11.runtime;

import com.endoflineblog.truffle.part_11.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.SubstringMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.root.BuiltInFuncRootNode;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.stream.IntStream;

@ExportLibrary(InteropLibrary.class)
public final class StringObject implements TruffleObject {
    private final String value;
    private final EasyScriptTruffleLanguage easyScriptTruffleLanguage;
    private final FunctionObject charAtMethod;
    private final FunctionObject substringMethod;

    public StringObject(String value, EasyScriptTruffleLanguage easyScriptTruffleLanguage) {
        this.value = value;
        this.easyScriptTruffleLanguage = easyScriptTruffleLanguage;
        this.charAtMethod = this.createMethod(CharAtMethodBodyExprNodeFactory.getInstance());
        this.substringMethod = this.createMethod(SubstringMethodBodyExprNodeFactory.getInstance());
    }

    public StringObject charAt(int index) {
        return new StringObject(index >= 0 && index < this.value.length()
                    ? this.value.substring(index, index + 1)
                    : "",
                this.easyScriptTruffleLanguage);
    }

    public StringObject substring(int start) {
        return new StringObject(
                this.value.substring(start),
                this.easyScriptTruffleLanguage);
    }

    public StringObject substring(int start, int end) {
        return new StringObject(
                this.value.substring(start, end),
                this.easyScriptTruffleLanguage);
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
        return "length".equals(member) || "charAt".equals(member) ||
                "substring".equals(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        switch (member) {
            case "length": return this.value.length();
            case "charAt": return this.charAtMethod;
            case "substring": return this.substringMethod;
            default: throw UnknownIdentifierException.create(member);
        }
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new String[]{"length", "charAt", "substring"});
    }

    private FunctionObject createMethod(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        int methodArgNr = nodeFactory.getExecutionSignature().size();
        ReadFunctionArgExprNode[] methodArguments = IntStream.range(0, methodArgNr)
                .mapToObj(ReadFunctionArgExprNode::new)
                .toArray(ReadFunctionArgExprNode[]::new);
        var charAtBody = nodeFactory.createNode(methodArguments, this);
        var charAtRootNode = new BuiltInFuncRootNode(this.easyScriptTruffleLanguage, charAtBody);
        return new FunctionObject(charAtRootNode.getCallTarget(), methodArgNr);
    }
}
