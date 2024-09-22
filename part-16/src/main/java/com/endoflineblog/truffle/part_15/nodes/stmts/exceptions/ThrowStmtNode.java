package com.endoflineblog.truffle.part_15.nodes.stmts.exceptions;

import com.endoflineblog.truffle.part_15.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_15.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_15.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_15.runtime.JavaScriptObject;
import com.endoflineblog.truffle.part_15.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleStackTrace;
import com.oracle.truffle.api.TruffleStackTraceElement;
import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.api.strings.TruffleStringBuilder;

import java.util.List;

/**
 * This Node represents the implementation of the {@code throw} statement.
 */
public abstract class ThrowStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    @Executed
    protected EasyScriptExprNode exceptionExpr;

    private final SourceSection sourceSection;

    protected ThrowStmtNode(EasyScriptExprNode exceptionExpr, SourceSection sourceSection) {
        this.exceptionExpr = exceptionExpr;
        this.sourceSection = sourceSection;
    }

    @Specialization(limit = "2")
    protected Object throwJavaScriptObject(
            JavaScriptObject value,
            @CachedLibrary("value") DynamicObjectLibrary nameObjectLibrary,
            @CachedLibrary("value") DynamicObjectLibrary messageObjectLibrary,
            @CachedLibrary("value") DynamicObjectLibrary stackObjectLibrary) {
        Object name = nameObjectLibrary.getOrDefault(value, "name", null);
        Object message = messageObjectLibrary.getOrDefault(value, "message", null);
        var easyScriptException = new EasyScriptException(name, message, value, this);
        stackObjectLibrary.put(value, "stack", this.formStackTrace(name, message, easyScriptException));
        throw easyScriptException;
    }

    @Specialization
    protected Object throwNonJavaScriptObject(Object value) {
        throw new EasyScriptException(value, this);
    }

    @TruffleBoundary
    private TruffleString formStackTrace(Object name, Object message, EasyScriptException easyScriptException) {
        TruffleStringBuilder sb = EasyScriptTruffleStrings.builder();
        sb.appendJavaStringUTF16Uncached(String.valueOf(name));
        if (message != Undefined.INSTANCE) {
            sb.appendJavaStringUTF16Uncached(": ");
            sb.appendJavaStringUTF16Uncached(String.valueOf(message));
        }
        List<TruffleStackTraceElement> truffleStackTraceEls = TruffleStackTrace.getStackTrace(easyScriptException);
        for (TruffleStackTraceElement truffleStackTracEl : truffleStackTraceEls) {
            sb.appendJavaStringUTF16Uncached("\n\tat ");

            Node location = truffleStackTracEl.getLocation();
            RootNode rootNode = location.getRootNode();
            String funcName = rootNode.getName();
            // we want to ignore the top-level program RootNode name in this stack trace
            boolean isFunc = !":program".equals(funcName);
            if (isFunc) {
                sb.appendJavaStringUTF16Uncached(funcName);
                sb.appendJavaStringUTF16Uncached(" (");
            }

            SourceSection sourceSection = location.getEncapsulatingSourceSection();
            sb.appendJavaStringUTF16Uncached(sourceSection.getSource().getName());
            sb.appendJavaStringUTF16Uncached(":");
            sb.appendJavaStringUTF16Uncached(String.valueOf(sourceSection.getStartLine()));
            sb.appendJavaStringUTF16Uncached(":");
            sb.appendJavaStringUTF16Uncached(String.valueOf(sourceSection.getStartColumn()));

            if (isFunc) {
                sb.appendJavaStringUTF16Uncached(")");
            }
        }
        return sb.toStringUncached();
    }

    @Override
    public SourceSection getSourceSection() {
        return this.sourceSection;
    }
}
