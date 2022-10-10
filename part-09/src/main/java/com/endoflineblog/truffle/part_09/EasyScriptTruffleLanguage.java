package com.endoflineblog.truffle.part_09;

import com.endoflineblog.truffle.part_09.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_09.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_09.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_09.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_09.nodes.root.BuiltInFuncRootNode;
import com.endoflineblog.truffle.part_09.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_09.parsing.EasyScriptTruffleParser;
import com.endoflineblog.truffle.part_09.parsing.ParsingResult;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;

import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Basically identical to the class with the same name from part 8,
 * the only difference is adjusting the code that creates the built-in functions
 * to account for changes in the API of {@link com.endoflineblog.truffle.part_09.runtime.GlobalScopeObject#registerFunction}.
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    private static final LanguageReference<EasyScriptTruffleLanguage> REF =
            LanguageReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language instance for the given {@link Node}. */
    public static EasyScriptTruffleLanguage get(Node node) {
        return REF.get(node);
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ParsingResult parsingResult = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var programRootNode = new StmtBlockRootNode(this, parsingResult.topLevelFrameDescriptor,
                parsingResult.programStmtBlock);
        return Truffle.getRuntime().createCallTarget(programRootNode);
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var context = new EasyScriptLanguageContext();

        this.defineBuiltInFunction(context, "Math.abs", AbsFunctionBodyExprNodeFactory.getInstance());
        this.defineBuiltInFunction(context, "Math.pow", PowFunctionBodyExprNodeFactory.getInstance());

        return context;
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }

    private void defineBuiltInFunction(EasyScriptLanguageContext context, String name,
            NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        int argumentCount = nodeFactory.getExecutionSignature().size();
        ReadFunctionArgExprNode[] functionArguments = IntStream.range(0, argumentCount)
                .mapToObj(i -> new ReadFunctionArgExprNode(i))
                .toArray(ReadFunctionArgExprNode[]::new);
        context.globalScopeObject.registerFunction(
                name,
                Truffle.getRuntime().createCallTarget(new BuiltInFuncRootNode(this,
                        nodeFactory.createNode((Object) functionArguments))),
                argumentCount);
    }
}
