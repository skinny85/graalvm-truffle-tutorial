package com.endoflineblog.truffle.part_06;

import com.endoflineblog.truffle.part_06.nodes.FunctionRootNode;
import com.endoflineblog.truffle.part_06.nodes.ProgramRootNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in.BuiltInFunctionBodyExpr;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_06.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_06.runtime.FunctionObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;

import java.util.List;
import java.util.stream.IntStream;

@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        List<EasyScriptStmtNode> stmts = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var programRootNode = new ProgramRootNode(this, stmts);
        return Truffle.getRuntime().createCallTarget(programRootNode);
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var context = new EasyScriptLanguageContext();

        context.globalScopeObject.newConstant("Math.abs",
                new FunctionObject(Truffle.getRuntime().createCallTarget(new FunctionRootNode(this,
                        AbsFunctionBodyExprNodeGen.create(new ReadFunctionArgExprNode(0))))));
        this.defineBuiltInFunction(context, "Math.pow", PowFunctionBodyExprNodeFactory.getInstance());

        return context;
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }

    private void defineBuiltInFunction(EasyScriptLanguageContext context, String name,
            NodeFactory<? extends BuiltInFunctionBodyExpr> nodeFactory) {
        ReadFunctionArgExprNode[] functionArguments = IntStream.range(0, nodeFactory.getExecutionSignature().size())
                .mapToObj(i -> new ReadFunctionArgExprNode(i))
                .toArray(ReadFunctionArgExprNode[]::new);
        context.globalScopeObject.newConstant(name,
                new FunctionObject(Truffle.getRuntime().createCallTarget(new FunctionRootNode(this,
                        nodeFactory.createNode((Object) functionArguments)))));
    }
}
