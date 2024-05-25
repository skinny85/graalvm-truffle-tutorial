package com.endoflineblog.truffle.part_14;

import com.endoflineblog.truffle.part_14.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_14.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in.AbsFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in.PowFunctionBodyExprNodeFactory;
import com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in.methods.HasOwnPropertyMethodBodyExprNodeFactory;
import com.endoflineblog.truffle.part_14.nodes.root.BuiltInFuncRootNode;
import com.endoflineblog.truffle.part_14.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_14.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_14.parsing.EasyScriptTruffleParser;
import com.endoflineblog.truffle.part_14.parsing.ParsingResult;
import com.endoflineblog.truffle.part_14.runtime.ArrayObject;
import com.endoflineblog.truffle.part_14.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_14.runtime.FunctionObject;
import com.endoflineblog.truffle.part_14.runtime.GlobalScopeObject;
import com.endoflineblog.truffle.part_14.runtime.JavaScriptObject;
import com.endoflineblog.truffle.part_14.runtime.ObjectPrototype;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import java.util.Collections;
import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Very similar to the class with the same name from part 13,
 * the only differences are adding the {@link #objectPrototype}
 * field of type {@link ObjectPrototype},
 * which we pass to the {@link EasyScriptTruffleParser#parse} method,
 * and use when creating prototypes of arrays and functions
 * (after adding the {@code hasOwnProperty} method,
 * implemented by {@link com.endoflineblog.truffle.part_14.nodes.exprs.functions.built_in.methods.HasOwnPropertyMethodBodyExprNode},
 * to it), and creating an instance of an empty function,
 * which we pass when creating an {@link EasyScriptLanguageContext}
 * returned from the {@link #createContext} method.
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
     * The root {@link Shape} for {@link GlobalScopeObject}
     * and {@link ClassPrototypeObject}.
     */
    private final Shape rootShape = Shape.newBuilder().build();

    private final ObjectPrototype objectPrototype = new ObjectPrototype(this.rootShape);

    private final ClassPrototypeObject functionPrototype = new ClassPrototypeObject(this.rootShape,
            "Function", this.objectPrototype);

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ParsingResult parsingResult = EasyScriptTruffleParser.parse(
                request.getSource().getReader(), this.rootShape, this.objectPrototype);
        var programRootNode = new StmtBlockRootNode(this, parsingResult.topLevelFrameDescriptor,
                parsingResult.programStmtBlock);
        return programRootNode.getCallTarget();
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var objectLibrary = DynamicObjectLibrary.getUncached();
        return new EasyScriptLanguageContext(
                this.createGlobalScopeObject(objectLibrary),
                this.createShapesAndPrototypes(objectLibrary),
                // empty function, used for default constructors by SuperExprNode
                new FunctionObject(
                        this.rootShape,
                        this.functionPrototype,
                        new StmtBlockRootNode(
                                this,
                                FrameDescriptor.newBuilder().build(),
                                new BlockStmtNode(Collections.emptyList())).getCallTarget(),
                        0));
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }

    private DynamicObject createGlobalScopeObject(DynamicObjectLibrary objectLibrary) {
        var globalScopeObject = new GlobalScopeObject(this.rootShape);
        // the 0 flag indicates that these are variables, and can be reassigned
        objectLibrary.putConstant(globalScopeObject, "Math",
                this.createMathObject(objectLibrary), 0);

        // initialize the Object prototype
        objectLibrary.putConstant(this.objectPrototype, "hasOwnProperty",
                this.defineBuiltInMethod(HasOwnPropertyMethodBodyExprNodeFactory.getInstance()),
                0);
        objectLibrary.putConstant(globalScopeObject, "Object",
                this.objectPrototype, 0);

        return globalScopeObject;
    }

    private Object createMathObject(DynamicObjectLibrary objectLibrary) {
        var mathPrototype = new ClassPrototypeObject(this.rootShape, "Math", this.objectPrototype);
        var mathObject = new JavaScriptObject(this.rootShape, mathPrototype);
        objectLibrary.putConstant(mathObject, "abs",
                this.defineBuiltInFunction(AbsFunctionBodyExprNodeFactory.getInstance()),
                0);
        objectLibrary.putConstant(mathObject, "pow",
                this.defineBuiltInFunction(PowFunctionBodyExprNodeFactory.getInstance()),
                0);
        return mathObject;
    }

    private ShapesAndPrototypes createShapesAndPrototypes(DynamicObjectLibrary objectLibrary) {
        var arrayPrototype = new ClassPrototypeObject(this.rootShape, "Array", this.objectPrototype);
        return new ShapesAndPrototypes(this.rootShape, this.arrayShape,
                this.objectPrototype, this.functionPrototype,
                arrayPrototype, this.createStringPrototype(objectLibrary));
    }

    private ClassPrototypeObject createStringPrototype(DynamicObjectLibrary objectLibrary) {
        var stringPrototype = new ClassPrototypeObject(this.rootShape, "String", this.objectPrototype);
        objectLibrary.putConstant(stringPrototype, "charAt",
                this.defineBuiltInMethod(CharAtMethodBodyExprNodeFactory.getInstance()),
                0);
        return stringPrototype;
    }

    private FunctionObject defineBuiltInFunction(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        return new FunctionObject(this.rootShape, this.functionPrototype,
                this.createCallTarget(nodeFactory, /* offsetArguments */ true),
                nodeFactory.getExecutionSignature().size());
    }

    private FunctionObject defineBuiltInMethod(NodeFactory<? extends BuiltInFunctionBodyExprNode> nodeFactory) {
        return new FunctionObject(this.rootShape, this.functionPrototype,
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
