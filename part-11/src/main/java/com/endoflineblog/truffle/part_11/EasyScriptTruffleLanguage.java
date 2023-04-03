package com.endoflineblog.truffle.part_11;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.root.BuiltInFuncRootNode;
import com.endoflineblog.truffle.part_11.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_11.parsing.EasyScriptTruffleParser;
import com.endoflineblog.truffle.part_11.parsing.ParsingResult;
import com.endoflineblog.truffle.part_11.runtime.ArrayObject;
import com.endoflineblog.truffle.part_11.runtime.FunctionObject;
import com.endoflineblog.truffle.part_11.runtime.MathObject;
import com.endoflineblog.truffle.part_11.runtime.StringPrototype;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Very similar to the class with the same name from part 10,
 * the only difference is we create a {@link StringPrototype}
 * object that contains the {@link CallTarget}s
 * for the built-in methods of strings
 * (we only support {@code charAt()} currently),
 * and store it in the {@code stringPrototype} field of {@link EasyScriptLanguageContext}.
 * We create the {@code charAt()} {@link CallTarget}
 * in a very similar way to the built-in functions for {@code Math}
 * that we've supported since part 6.
 *
 * @see com.endoflineblog.truffle.part_11.EasyScriptLanguageContext#stringPrototype
 * @see com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNode
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    private static final LanguageReference<EasyScriptTruffleLanguage> REF =
            LanguageReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language instance for the given {@link Node}. */
    public static EasyScriptTruffleLanguage get(Node node) {
        return REF.get(node);
    }

    /** The root {@link Shape} for {@link ArrayObject} */
    private final Shape arrayShape = Shape.newBuilder().layout(ArrayObject.class).build();

    /** The root {@link Shape} for {@link com.endoflineblog.truffle.part_11.runtime.GlobalScopeObject}. */
    private final Shape globalScopeShape = Shape.newBuilder().build();

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ParsingResult parsingResult = EasyScriptTruffleParser.parse(
                request.getSource().getReader(), this.arrayShape);
        var programRootNode = new StmtBlockRootNode(this, parsingResult.topLevelFrameDescriptor,
                parsingResult.programStmtBlock);
        return programRootNode.getCallTarget();
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var context = new EasyScriptLanguageContext(this.globalScopeShape, this.createStringPrototype());
        var globalScopeObject = context.globalScopeObject;

        var objectLibrary = DynamicObjectLibrary.getUncached();
        // the 1 flag indicates Math is a constant, and cannot be reassigned
        objectLibrary.putConstant(globalScopeObject, "Math", MathObject.create(this,
            this.defineBuiltInFunction(AbsFunctionBodyExprNodeFactory.getInstance()),
            this.defineBuiltInFunction(PowFunctionBodyExprNodeFactory.getInstance())), 1);

        return context;
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }

    private StringPrototype createStringPrototype() {
        return new StringPrototype(
                this.createCallTarget(CharAtMethodBodyExprNodeFactory.getInstance()));
    }

    private FunctionObject defineBuiltInFunction(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        return new FunctionObject(this.createCallTarget(nodeFactory),
                nodeFactory.getExecutionSignature().size());
    }

    private CallTarget createCallTarget(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        int argumentCount = nodeFactory.getExecutionSignature().size();
        ReadFunctionArgExprNode[] functionArguments = IntStream.range(0, argumentCount)
                .mapToObj(i -> new ReadFunctionArgExprNode(i))
                .toArray(ReadFunctionArgExprNode[]::new);
        var rootNode = new BuiltInFuncRootNode(this,
                nodeFactory.createNode((Object) functionArguments));
        return rootNode.getCallTarget();
    }
}
