package com.endoflineblog.truffle.part_13;

import com.endoflineblog.truffle.part_13.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_13.nodes.root.BuiltInFuncRootNode;
import com.endoflineblog.truffle.part_13.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_13.parsing.EasyScriptTruffleParser;
import com.endoflineblog.truffle.part_13.parsing.ParsingResult;
import com.endoflineblog.truffle.part_13.runtime.ArrayObject;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_13.runtime.FunctionObject;
import com.endoflineblog.truffle.part_13.runtime.MathObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Very similar to the class with the same name from part 11,
 * the only difference is that we rename the field {@code globalScopeShape}
 * to {@code rootShape}, as it's now used as the {@link Shape}
 * of the {@link com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject}
 * {@link com.oracle.truffle.api.object.DynamicObject},
 * in addition to the {@link com.endoflineblog.truffle.part_13.runtime.GlobalScopeObject},
 * and we also pass it to the
 * {@link com.endoflineblog.truffle.part_13.parsing.EasyScriptTruffleParser#parse main parsing method}.
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

    /**
     * The root {@link Shape} for {@link com.endoflineblog.truffle.part_13.runtime.GlobalScopeObject}
     * and {@link com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject}.
     */
    private final Shape rootShape = Shape.newBuilder().build();

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ParsingResult parsingResult = EasyScriptTruffleParser.parse(
                request.getSource().getReader(), this.rootShape, this.arrayShape);
        var programRootNode = new StmtBlockRootNode(this, parsingResult.topLevelFrameDescriptor,
                parsingResult.programStmtBlock);
        return programRootNode.getCallTarget();
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var objectLibrary = DynamicObjectLibrary.getUncached();

        var context = new EasyScriptLanguageContext(this.rootShape,
                this.createStringPrototype(objectLibrary));
        var globalScopeObject = context.globalScopeObject;

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

    private ClassPrototypeObject createStringPrototype(DynamicObjectLibrary objectLibrary) {
        var stringPrototype = new ClassPrototypeObject(this.rootShape, "String");
        objectLibrary.putConstant(stringPrototype, "charAt",
                this.defineBuiltInMethod(CharAtMethodBodyExprNodeFactory.getInstance()),
                0);
        return stringPrototype;
    }

    private FunctionObject defineBuiltInFunction(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        return new FunctionObject(this.createCallTarget(nodeFactory, /* offsetArguments */ true),
                nodeFactory.getExecutionSignature().size());
    }

    private FunctionObject defineBuiltInMethod(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        return new FunctionObject(
                // built-in method implementation Nodes already have an argument for `this`,
                // so there's no need to offset the method arguments
                this.createCallTarget(nodeFactory, /* offsetArguments */ false),
                // we always add an extra argument for 'this' inside FunctionDispatchNode,
                // but built-in methods already have 'this' in their specializations -
                // for that reason, we make the FunctionObject have one argument less than the specializations take
                nodeFactory.getExecutionSignature().size() - 1);
    }

    private CallTarget createCallTarget(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory,
            boolean offsetArguments) {
        int argumentCount = nodeFactory.getExecutionSignature().size();
        ReadFunctionArgExprNode[] functionArguments = IntStream.range(0, argumentCount)
                .mapToObj(i -> new ReadFunctionArgExprNode(offsetArguments ? i + 1 : i))
                .toArray(ReadFunctionArgExprNode[]::new);
        var rootNode = new BuiltInFuncRootNode(this,
                nodeFactory.createNode((Object) functionArguments));
        return rootNode.getCallTarget();
    }
}
