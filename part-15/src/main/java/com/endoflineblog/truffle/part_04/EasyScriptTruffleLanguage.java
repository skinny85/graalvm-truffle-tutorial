package com.endoflineblog.truffle.part_04;

import com.endoflineblog.truffle.part_04.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_04.nodes.EasyScriptRootNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;

/**
 * This class allows your programming language to become a part of the GraalVM polyglot API.
 * The {@code id} used below in the {@link Registration}
 * annotation is the first argument passed to the
 * {@link org.graalvm.polyglot.Context#eval(String, CharSequence)}
 * method.
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<Void> {
    /**
     * This method is the callback used by the GraalVM polyglot API
     * when the language with ID "ezs" is
     * {@link org.graalvm.polyglot.Context#eval(String, CharSequence) evaluated}.
     * It needs to perform the parsing of the code passed in the request,
     * and return a {@link CallTarget} that represents the entrypoint of your language.
     * The polyglot API will then {@link CallTarget#call call} this {@code CallTarget},
     * and return that as the result of evaluating your program.
     */
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        EasyScriptNode exprNode = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var rootNode = new EasyScriptRootNode(this, exprNode);
        return rootNode.getCallTarget();
    }

    /**
     * This is an abstract method in {@link TruffleLanguage},
     * so we have to override it,
     * but we don't need a Context for EasyScript yet,
     * so we just return {@code null}.
     */
    @Override
    protected Void createContext(Env env) {
        return null;
    }
}
