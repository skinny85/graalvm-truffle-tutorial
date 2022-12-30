package com.endoflineblog.truffle.part_08;

import com.endoflineblog.truffle.part_08.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_08.nodes.root.BuiltInFuncRootNode;
import com.endoflineblog.truffle.part_08.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_08.parsing.EasyScriptTruffleParser;
import com.endoflineblog.truffle.part_08.parsing.ParsingResult;
import com.endoflineblog.truffle.part_08.runtime.FunctionObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;

import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Basically identical to the class with the same name from part 7,
 * the only difference is creating the main {@link com.oracle.truffle.api.nodes.RootNode}
 * with a {@link com.oracle.truffle.api.frame.FrameDescriptor}
 * returned by {@link EasyScriptTruffleParser#parse}.
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
        return programRootNode.getCallTarget();
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
        var builtInFuncRootNode = new BuiltInFuncRootNode(this,
                nodeFactory.createNode((Object) functionArguments));
        context.globalScopeObject.newFunction(name,
                new FunctionObject(builtInFuncRootNode.getCallTarget(), argumentCount));
    }
}
