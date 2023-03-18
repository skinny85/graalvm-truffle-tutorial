package com.endoflineblog.truffle.part_11;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.SubstringMethodBodyExprNodeFactory;
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
 * Very similar to the class with the same name from part 9,
 * the only differences are to account for changes in the built-in {@link MathObject}
 * (we no longer have bindings like {@code Math.pow} and {@code Math.abs},
 * instead {@code Math} is a static object with two properties,
 * {@code abs} and {@code pow}, that we read from in
 * {@link com.endoflineblog.truffle.part_11.nodes.exprs.properties.PropertyReadExprNode}),
 * and caching the root shapes for the {@link ArrayObject}
 * and {@link com.endoflineblog.truffle.part_11.runtime.GlobalScopeObject}.
 *
 * @see MathObject
 * @see com.endoflineblog.truffle.part_11.nodes.exprs.properties.PropertyReadExprNode
 * @see #arrayShape
 * @see #globalScopeShape
 * @see ArrayObject
 * @see com.endoflineblog.truffle.part_11.runtime.GlobalScopeObject
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
                this.createCallTarget(CharAtMethodBodyExprNodeFactory.getInstance()),
                this.createCallTarget(SubstringMethodBodyExprNodeFactory.getInstance()));
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
