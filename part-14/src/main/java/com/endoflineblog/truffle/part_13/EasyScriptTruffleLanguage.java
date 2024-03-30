package com.endoflineblog.truffle.part_13;

import com.endoflineblog.truffle.part_13.common.ShapesAndPrototypes;
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
import com.endoflineblog.truffle.part_13.runtime.GlobalScopeObject;
import com.endoflineblog.truffle.part_13.runtime.JavaScriptObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import java.util.stream.IntStream;

/**
 * The {@link TruffleLanguage} implementation for this part of the article series.
 * Very similar to the class with the same name from part 12,
 * with just a few small differences:
 * <ul>
 *     <li>
 *         We make sure to initialize the new {@link ShapesAndPrototypes} class
 *         with all of the shapes and prototypes we need,
 *         now that we've made {@link FunctionObject} and {@link ArrayObject}
 *         extend {@link JavaScriptObject},
 *         and add to it the {@link EasyScriptLanguageContext Truffle language context}.
 *     </li>
 *     <li>
 *         We offset arguments of built-in functions by 1,
 *         while changing the reported number of arguments for built-in methods also by 1,
 *         to account for the changes made in {@link com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionDispatchNode}
 *         to support {@code this}.
 *     </li>
 *     <li>
 *         We make {@code MathObject} just a regular {@link JavaScriptObject},
 *         instead of a static object, since it now can have properties written to it.
 *     </li>
 *     <li>
 *         We make the String prototype just a regular {@link ClassPrototypeObject},
 *         instead of having a dedicated class for it.
 *     </li>
 * </ul>
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

    private final ClassPrototypeObject functionPrototype = new ClassPrototypeObject(this.rootShape, "Function");

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ParsingResult parsingResult = EasyScriptTruffleParser.parse(
                request.getSource().getReader(), this.rootShape);
        var programRootNode = new StmtBlockRootNode(this, parsingResult.topLevelFrameDescriptor,
                parsingResult.programStmtBlock);
        return programRootNode.getCallTarget();
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var objectLibrary = DynamicObjectLibrary.getUncached();
        return new EasyScriptLanguageContext(
                this.createGlobalScopeObject(objectLibrary),
                this.createShapesAndPrototypes(objectLibrary));
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }

    private DynamicObject createGlobalScopeObject(DynamicObjectLibrary objectLibrary) {
        var globalScopeObject = new GlobalScopeObject(this.rootShape);
        // the 0 flag indicates Math is a variable, and can be reassigned
        objectLibrary.putConstant(globalScopeObject, "Math",
                this.createMathObject(objectLibrary), 0);
        return globalScopeObject;
    }

    private Object createMathObject(DynamicObjectLibrary objectLibrary) {
        var mathPrototype = new ClassPrototypeObject(this.rootShape, "Math");
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
        var arrayPrototype = new ClassPrototypeObject(this.rootShape, "Array");
        return new ShapesAndPrototypes(this.rootShape, this.arrayShape,
                this.functionPrototype, arrayPrototype,
                this.createStringPrototype(objectLibrary));
    }

    private ClassPrototypeObject createStringPrototype(DynamicObjectLibrary objectLibrary) {
        var stringPrototype = new ClassPrototypeObject(this.rootShape, "String");
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
