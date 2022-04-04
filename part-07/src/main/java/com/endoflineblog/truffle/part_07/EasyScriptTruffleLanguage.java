package com.endoflineblog.truffle.part_07;

import com.endoflineblog.truffle.part_07.nodes.FunctionRootNode;
import com.endoflineblog.truffle.part_07.nodes.ProgramRootNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_07.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_07.runtime.FunctionObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Very similar to the class with the same name from part 5,
 * the only difference is that this adds the supported built-in functions
 * ({@code Math.abs} and {@code Math.pow})
 * to the global scope in the {@link #createContext} method.
 *
 * @see #createContext
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        List<EasyScriptStmtNode> stmts = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var programRootNode = new ProgramRootNode(this, stmts);
        return Truffle.getRuntime().createCallTarget(programRootNode);
    }

    /**
     * We create a new instance of {@link EasyScriptLanguageContext},
     * and return it, but not before adding the built-in functions to
     * the global scope the context object holds.
     */
    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var context = new EasyScriptLanguageContext();

        // an example of defining a function directly, without @GenerateNodeFactory
        context.globalScopeObject.newConstant("Math.abs",
                new FunctionObject(Truffle.getRuntime().createCallTarget(new FunctionRootNode(this,
                        AbsFunctionBodyExprNodeGen.create(new ReadFunctionArgExprNode(0))))));
        // an example of using our utility method, possible by annotating the Node class with @GenerateNodeFactory
        this.defineBuiltInFunction(context, "Math.pow", PowFunctionBodyExprNodeFactory.getInstance());

        return context;
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }

    private void defineBuiltInFunction(EasyScriptLanguageContext context, String name,
            NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        ReadFunctionArgExprNode[] functionArguments = IntStream.range(0, nodeFactory.getExecutionSignature().size())
                .mapToObj(i -> new ReadFunctionArgExprNode(i))
                .toArray(ReadFunctionArgExprNode[]::new);
        context.globalScopeObject.newConstant(name,
                new FunctionObject(Truffle.getRuntime().createCallTarget(new FunctionRootNode(this,
                        nodeFactory.createNode((Object) functionArguments)))));
    }
}
