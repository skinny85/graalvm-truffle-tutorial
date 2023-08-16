package com.endoflineblog.truffle.part_12.parsing;

import com.endoflineblog.truffle.part_12.common.DeclarationKind;
import com.endoflineblog.truffle.part_12.common.LocalVariableFrameSlotId;
import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.GlobalScopeObjectExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic.NegationExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic.NegationExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.arithmetic.SubtractionExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.arrays.ArrayIndexReadExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.arrays.ArrayIndexWriteExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.arrays.ArrayLiteralExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.comparisons.EqualityExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.comparisons.GreaterExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.comparisons.GreaterOrEqualExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.comparisons.InequalityExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.comparisons.LesserExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.comparisons.LesserOrEqualExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.functions.WriteFunctionArgExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.literals.BoolLiteralExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.literals.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.literals.IntLiteralExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.literals.StringLiteralExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.literals.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.properties.PropertyReadExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.variables.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.variables.GlobalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.variables.LocalVarAssignmentExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.variables.LocalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.exprs.variables.LocalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_12.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.controlflow.BreakStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.controlflow.ContinueStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.controlflow.IfStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.controlflow.ReturnStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.loops.DoWhileStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.loops.ForStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.loops.WhileStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.variables.FuncDeclStmtNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.variables.FuncDeclStmtNodeGen;
import com.endoflineblog.truffle.part_12.nodes.stmts.variables.GlobalVarDeclStmtNodeGen;
import com.endoflineblog.truffle.part_12.parsing.antlr.EasyScriptLexer;
import com.endoflineblog.truffle.part_12.parsing.antlr.EasyScriptParser;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.object.Shape;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * This is the class that parses the program and turns it into a Truffle AST.
 * It uses ANTLR to perform the actual parsing,
 * with the grammar defined in the {@code src/main/antlr/com/endoflineblog/truffle/part_12/parsing/antlr/EasyScript.g4} file.
 * This class is invoked by the {@code TruffleLanguage} implementation for this part.
 *
 * @see #parse
 * @see com.endoflineblog.truffle.part_12.EasyScriptTruffleLanguage
 */
public final class EasyScriptTruffleParser {
    public static ParsingResult parse(Reader program, Shape arrayShape) throws IOException {
        var lexer = new EasyScriptLexer(CharStreams.fromReader(program));
        // remove the default console error listener
        lexer.removeErrorListeners();
        var parser = new EasyScriptParser(new CommonTokenStream(lexer));
        // remove the default console error listener
        parser.removeErrorListeners();
        // throw an exception when a parsing error is encountered
        parser.setErrorHandler(new BailErrorStrategy());
        var easyScriptTruffleParser = new EasyScriptTruffleParser(arrayShape);
        List<EasyScriptStmtNode> stmts = easyScriptTruffleParser.parseStmtsList(parser.start().stmt());
        return new ParsingResult(
                new BlockStmtNode(stmts),
                easyScriptTruffleParser.frameDescriptor.build());
    }

    private final Shape arrayShape;

    private enum ParserState { TOP_LEVEL, NESTED_SCOPE_IN_TOP_LEVEL, FUNC_DEF }

    /** Whether we're parsing a function definition. */
    private ParserState state;

    /**
     * The {@link FrameDescriptor} for either a given function definition,
     * or for any local variables in the statements of the top-level program.
     */
    private FrameDescriptor.Builder frameDescriptor;

    private static abstract class FrameMember {}
    private static final class FunctionArgument extends FrameMember {
        public final int argumentIndex;

        FunctionArgument(int argumentIndex) {
            this.argumentIndex = argumentIndex;
        }
    }
    private static final class LocalVariable extends FrameMember {
        public final int variableIndex;
        public final DeclarationKind declarationKind;

        LocalVariable(int variableIndex, DeclarationKind declarationKind) {
            this.variableIndex = variableIndex;
            this.declarationKind = declarationKind;
        }
    }

    /**
     * Map containing bindings for the function arguments and local variables when parsing function definitions
     * and nested scopes of the top-level scope.
     */
    private Stack<Map<String, FrameMember>> localScopes;

    /**
     * The counter that makes it easy to generate unique variable names for local variables
     * (as their names can repeat in nested scopes).
     */
    private int localVariablesCounter;

    private EasyScriptTruffleParser(Shape arrayShape) {
        this.arrayShape = arrayShape;
        this.state = ParserState.TOP_LEVEL;
        this.frameDescriptor = FrameDescriptor.newBuilder();
        this.localScopes = new Stack<>();
        this.localVariablesCounter = 0;
    }

    private List<EasyScriptStmtNode> parseStmtsList(List<EasyScriptParser.StmtContext> stmts) {
        // in the first pass, only handle function declarations,
        // as it's legal to invoke functions before they are declared
        var funcDecls = new ArrayList<FuncDeclStmtNode>();
        for (EasyScriptParser.StmtContext stmt : stmts) {
            if (stmt instanceof EasyScriptParser.FuncDeclStmtContext) {
                funcDecls.add(this.parseFuncDeclStmt((EasyScriptParser.FuncDeclStmtContext) stmt));
            }
        }

        // in the second pass, handle the remaining statements that are not function declarations
        var nonFuncDeclStmts = new ArrayList<EasyScriptStmtNode>();
        for (EasyScriptParser.StmtContext stmt : stmts) {
            if (stmt instanceof EasyScriptParser.ExprStmtContext) {
                nonFuncDeclStmts.add(this.parseExprStmt((EasyScriptParser.ExprStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.ReturnStmtContext) {
                nonFuncDeclStmts.add(this.parseReturnStmt((EasyScriptParser.ReturnStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.IfStmtContext) {
                nonFuncDeclStmts.add(this.parseIfStmt((EasyScriptParser.IfStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.WhileStmtContext) {
                nonFuncDeclStmts.add(this.parseWhileStmt((EasyScriptParser.WhileStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.DoWhileStmtContext) {
                nonFuncDeclStmts.add(this.parseDoWhileStmt((EasyScriptParser.DoWhileStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.ForStmtContext) {
                nonFuncDeclStmts.add(this.parseForStmt((EasyScriptParser.ForStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.BlockStmtContext) {
                nonFuncDeclStmts.add(this.parseStmtBlock((EasyScriptParser.BlockStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.BreakStmtContext) {
                nonFuncDeclStmts.add(new BreakStmtNode());
            } else if (stmt instanceof EasyScriptParser.ContinueStmtContext) {
                nonFuncDeclStmts.add(new ContinueStmtNode());
            } else if (stmt instanceof EasyScriptParser.VarDeclStmtContext) {
                EasyScriptParser.VarDeclStmtContext varDeclStmt = (EasyScriptParser.VarDeclStmtContext) stmt;
                DeclarationKind declarationKind = DeclarationKind.fromToken(varDeclStmt.kind.getText());
                List<EasyScriptParser.BindingContext> varDeclBindings = varDeclStmt.binding();
                for (EasyScriptParser.BindingContext varBinding : varDeclBindings) {
                    String variableId = varBinding.ID().getText();
                    var bindingExpr = varBinding.expr1();
                    EasyScriptExprNode initializerExpr;
                    if (bindingExpr == null) {
                        if (declarationKind == DeclarationKind.CONST) {
                            throw new EasyScriptException("Missing initializer in const declaration '" + variableId + "'");
                        }
                        // if a 'let' or 'var' declaration is missing an initializer,
                        // it means it will be initialized with 'undefined'
                        initializerExpr = new UndefinedLiteralExprNode();
                    } else {
                        initializerExpr = this.parseExpr1(bindingExpr);
                    }

                    if (this.state == ParserState.TOP_LEVEL) {
                        // this is a global variable
                        nonFuncDeclStmts.add(GlobalVarDeclStmtNodeGen.create(GlobalScopeObjectExprNodeGen.create(), initializerExpr, variableId, declarationKind));
                    } else {
                        // this is a local variable (either of a function, or on the top-level)
                        var frameSlotId = new LocalVariableFrameSlotId(variableId, ++this.localVariablesCounter);
                        int frameSlot = this.frameDescriptor.addSlot(FrameSlotKind.Illegal, frameSlotId, declarationKind);
                        if (this.localScopes.peek().putIfAbsent(variableId, new LocalVariable(frameSlot, declarationKind)) != null) {
                            throw new EasyScriptException("Identifier '" + variableId + "' has already been declared");
                        }
                        LocalVarAssignmentExprNode assignmentExpr = LocalVarAssignmentExprNodeGen.create(initializerExpr, frameSlot);
                        nonFuncDeclStmts.add(new ExprStmtNode(assignmentExpr, /* discardExpressionValue */ true));
                    }
                }
            }
        }

        // return the function declarations first, and then the remaining statements
        var result = new ArrayList<EasyScriptStmtNode>(funcDecls.size() + nonFuncDeclStmts.size());
        result.addAll(funcDecls);
        result.addAll(nonFuncDeclStmts);
        return result;
    }

    private ExprStmtNode parseExprStmt(EasyScriptParser.ExprStmtContext exprStmt) {
        return new ExprStmtNode(this.parseExpr1(exprStmt.expr1()));
    }

    private ReturnStmtNode parseReturnStmt(EasyScriptParser.ReturnStmtContext returnStmt) {
        if (this.state != ParserState.FUNC_DEF) {
            throw new EasyScriptException("return statement is not allowed outside functions");
        }
        return new ReturnStmtNode(returnStmt.expr1() == null
                ? new UndefinedLiteralExprNode()
                : this.parseExpr1(returnStmt.expr1()));
    }

    private IfStmtNode parseIfStmt(EasyScriptParser.IfStmtContext ifStmt) {
        return new IfStmtNode(
                this.parseExpr1(ifStmt.cond),
                this.parseStmt(ifStmt.then_stmt),
                this.parseStmt(ifStmt.else_stmt));
    }

    private WhileStmtNode parseWhileStmt(EasyScriptParser.WhileStmtContext whileStmt) {
        return new WhileStmtNode(
                this.parseExpr1(whileStmt.cond),
                this.parseStmt(whileStmt.body));
    }

    private DoWhileStmtNode parseDoWhileStmt(EasyScriptParser.DoWhileStmtContext doWhileStmt) {
        return new DoWhileStmtNode(
                this.parseExpr1(doWhileStmt.cond),
                this.parseStmtBlock(doWhileStmt.stmt()));
    }

    private ForStmtNode parseForStmt(EasyScriptParser.ForStmtContext forStmt) {
        // a 'for' loop is its own scope
        ParserState previousParserState = this.state;

        if (this.state == ParserState.TOP_LEVEL) {
            this.state = ParserState.NESTED_SCOPE_IN_TOP_LEVEL;
        }
        this.localScopes.push(new HashMap<>());

        var ret = new ForStmtNode(
                this.parseStmt(forStmt.init),
                this.parseExpr1(forStmt.cond),
                this.parseExpr1(forStmt.updt),
                this.parseStmt(forStmt.body));

        // bring back the old state
        this.state = previousParserState;
        this.localScopes.pop();

        return ret;
    }

    private EasyScriptStmtNode parseStmt(EasyScriptParser.StmtContext stmt) {
        if (stmt == null) {
            return null;
        }
        List<EasyScriptStmtNode> parsedStmts = this.parseStmtsList(List.of(stmt));
        return parsedStmts.size() == 1
            ? parsedStmts.get(0)
            : new BlockStmtNode(parsedStmts);
    }

    private BlockStmtNode parseStmtBlock(EasyScriptParser.BlockStmtContext blockStmt) {
        return parseStmtBlock(blockStmt.stmt());
    }

    private BlockStmtNode parseStmtBlock(List<EasyScriptParser.StmtContext> stmts) {
        // save the current state of the parser (before entering the block)
        ParserState previousParserState = this.state;

        if (this.state == ParserState.TOP_LEVEL) {
            this.state = ParserState.NESTED_SCOPE_IN_TOP_LEVEL;
        }
        this.localScopes.push(new HashMap<>());

        // perform the parsing
        List<EasyScriptStmtNode> ret = this.parseStmtsList(stmts);

        // bring back the old state
        this.state = previousParserState;
        this.localScopes.pop();

        return new BlockStmtNode(ret);
    }

    private FuncDeclStmtNode parseFuncDeclStmt(EasyScriptParser.FuncDeclStmtContext funcDeclStmt) {
        if (this.state == ParserState.FUNC_DEF) {
            // we do not allow nested functions (yet 😉)
            throw new EasyScriptException("nested functions are not supported in EasyScript yet");
        }

        // save the current state of the parser (before entering the function)
        FrameDescriptor.Builder previousFrameDescriptor = this.frameDescriptor;
        ParserState previousParserState = this.state;
        var previousLocalScopes = this.localScopes;

        // initialize the new state
        this.frameDescriptor = FrameDescriptor.newBuilder();
        this.state = ParserState.FUNC_DEF;
        this.localScopes = new Stack<>();

        var localVariables = new HashMap<String, FrameMember>();
        // add each parameter to the map, with the correct index
        List<TerminalNode> funcArgs = funcDeclStmt.args.ID();
        int argumentCount = funcArgs.size();
        // first, initialize the locals with function arguments
        for (int i = 0; i < argumentCount; i++) {
            localVariables.put(funcArgs.get(i).getText(), new FunctionArgument(i));
        }
        this.localScopes.push(localVariables);

        // parse the statements in the function definition
        List<EasyScriptStmtNode> funcStmts = this.parseStmtsList(funcDeclStmt.stmt());

        FrameDescriptor frameDescriptor = this.frameDescriptor.build();
        // bring back the old state
        this.frameDescriptor = previousFrameDescriptor;
        this.state = previousParserState;
        this.localScopes = previousLocalScopes;

        return FuncDeclStmtNodeGen.create(GlobalScopeObjectExprNodeGen.create(),
                funcDeclStmt.name.getText(),
                frameDescriptor, new UserFuncBodyStmtNode(funcStmts), argumentCount);
    }

    private EasyScriptExprNode parseExpr1(EasyScriptParser.Expr1Context expr1) {
        if (expr1 == null) {
            return null;
        }
        if (expr1 instanceof EasyScriptParser.AssignmentExpr1Context) {
            return parseAssignmentExpr((EasyScriptParser.AssignmentExpr1Context) expr1);
        } else if (expr1 instanceof EasyScriptParser.ArrayIndexWriteExpr1Context) {
            return this.parseArrayIndexWriteExpr((EasyScriptParser.ArrayIndexWriteExpr1Context) expr1);
        } else {
            return parseExpr2(((EasyScriptParser.PrecedenceTwoExpr1Context) expr1).expr2());
        }
    }

    private EasyScriptExprNode parseAssignmentExpr(EasyScriptParser.AssignmentExpr1Context assignmentExpr) {
        String variableId = assignmentExpr.ID().getText();
        FrameMember frameMember = this.findFrameMember(variableId);
        EasyScriptExprNode initializerExpr = this.parseExpr1(assignmentExpr.expr1());
        if (frameMember == null) {
            return GlobalVarAssignmentExprNodeGen.create(GlobalScopeObjectExprNodeGen.create(), initializerExpr, variableId);
        } else {
            if (frameMember instanceof FunctionArgument) {
                return new WriteFunctionArgExprNode(initializerExpr, ((FunctionArgument) frameMember).argumentIndex);
            } else {
                var localVariable = (LocalVariable) frameMember;
                if (localVariable.declarationKind == DeclarationKind.CONST) {
                    throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
                }
                return LocalVarAssignmentExprNodeGen.create(initializerExpr, localVariable.variableIndex);
            }
        }
    }

    private EasyScriptExprNode parseArrayIndexWriteExpr(EasyScriptParser.ArrayIndexWriteExpr1Context arrayIndexWriteExpr) {
        return ArrayIndexWriteExprNodeGen.create(
                this.parseExpr5(arrayIndexWriteExpr.arr),
                this.parseExpr1(arrayIndexWriteExpr.index),
                this.parseExpr1(arrayIndexWriteExpr.rvalue));
    }

    private EasyScriptExprNode parseExpr2(EasyScriptParser.Expr2Context expr2) {
        return expr2 instanceof EasyScriptParser.EqNotEqExpr2Context
                ? this.parseEqNotEqExpression(((EasyScriptParser.EqNotEqExpr2Context) expr2))
                : this.parseExpr3(((EasyScriptParser.PrecedenceThreeExpr2Context) expr2).expr3());
    }

    private EasyScriptExprNode parseEqNotEqExpression(EasyScriptParser.EqNotEqExpr2Context eqNotEqExpr) {
        EasyScriptExprNode leftSide = this.parseExpr2(eqNotEqExpr.expr2());
        EasyScriptExprNode rightSide = this.parseExpr3(eqNotEqExpr.expr3());
        return "===".equals(eqNotEqExpr.c.getText())
                ? EqualityExprNodeGen.create(leftSide, rightSide)
                : InequalityExprNodeGen.create(leftSide, rightSide);
    }

    private EasyScriptExprNode parseExpr3(EasyScriptParser.Expr3Context expr3) {
        if (expr3 instanceof EasyScriptParser.ComparisonExpr3Context) {
            return this.parseComparisonExpr(((EasyScriptParser.ComparisonExpr3Context) expr3));
        } else {
            return this.parseExpr4(((EasyScriptParser.PrecedenceFourExpr3Context) expr3).expr4());
        }
    }

    private EasyScriptExprNode parseComparisonExpr(EasyScriptParser.ComparisonExpr3Context comparisonExpr) {
        EasyScriptExprNode leftSide = this.parseExpr3(comparisonExpr.expr3());
        EasyScriptExprNode rightSide = this.parseExpr4(comparisonExpr.expr4());
        switch (comparisonExpr.c.getText()) {
            case  "<": return LesserExprNodeGen.create(leftSide, rightSide);
            case "<=": return LesserOrEqualExprNodeGen.create(leftSide, rightSide);
            case  ">": return GreaterExprNodeGen.create(leftSide, rightSide);
            default:
            case ">=": return GreaterOrEqualExprNodeGen.create(leftSide, rightSide);
        }
    }

    private EasyScriptExprNode parseExpr4(EasyScriptParser.Expr4Context expr4) {
        if (expr4 instanceof EasyScriptParser.AddSubtractExpr4Context) {
            return this.parseAdditionSubtractionExpr((EasyScriptParser.AddSubtractExpr4Context) expr4);
        } else if (expr4 instanceof EasyScriptParser.UnaryMinusExpr4Context) {
            return parseUnaryMinusExpr((EasyScriptParser.UnaryMinusExpr4Context) expr4);
        } else {
            return parseExpr5(((EasyScriptParser.PrecedenceFiveExpr4Context) expr4).expr5());
        }
    }

    private EasyScriptExprNode parseAdditionSubtractionExpr(EasyScriptParser.AddSubtractExpr4Context addSubtractExpr) {
        EasyScriptExprNode leftSide = this.parseExpr4(addSubtractExpr.left);
        EasyScriptExprNode rightSide = this.parseExpr5(addSubtractExpr.right);
        switch (addSubtractExpr.o.getText()) {
            case "-":
                return SubtractionExprNodeGen.create(leftSide, rightSide);
            case "+":
            default:
                return AdditionExprNodeGen.create(leftSide, rightSide);
        }
    }

    private NegationExprNode parseUnaryMinusExpr(EasyScriptParser.UnaryMinusExpr4Context unaryMinusExpr) {
        return NegationExprNodeGen.create(parseExpr5(unaryMinusExpr.expr5()));
    }

    private EasyScriptExprNode parseExpr5(EasyScriptParser.Expr5Context expr5) {
        if (expr5 instanceof EasyScriptParser.LiteralExpr5Context) {
            return parseLiteralExpr((EasyScriptParser.LiteralExpr5Context) expr5);
        } else if (expr5 instanceof EasyScriptParser.ReferenceExpr5Context) {
            return parseReference(((EasyScriptParser.ReferenceExpr5Context) expr5).ID().getText());
        } else if (expr5 instanceof EasyScriptParser.PropertyReadExpr5Context) {
            return this.parsePropertyReadExpr((EasyScriptParser.PropertyReadExpr5Context) expr5);
        } else if (expr5 instanceof EasyScriptParser.ArrayLiteralExpr5Context) {
            return parseArrayLiteralExpr((EasyScriptParser.ArrayLiteralExpr5Context) expr5);
        } else if (expr5 instanceof EasyScriptParser.ArrayIndexReadExpr5Context) {
            return this.parseArrayIndexReadExpr((EasyScriptParser.ArrayIndexReadExpr5Context) expr5);
        } else if (expr5 instanceof EasyScriptParser.CallExpr5Context) {
            return parseCallExpr((EasyScriptParser.CallExpr5Context) expr5);
        } else {
            return parseExpr1(((EasyScriptParser.PrecedenceOneExpr5Context) expr5).expr1());
        }
    }

    private EasyScriptExprNode parseLiteralExpr(EasyScriptParser.LiteralExpr5Context literalExpr) {
        TerminalNode intTerminal = literalExpr.literal().INT();
        if (intTerminal != null) {
            return parseIntLiteral(intTerminal.getText());
        }
        TerminalNode doubleTerminal = literalExpr.literal().DOUBLE();
        if (doubleTerminal != null) {
            return parseDoubleLiteral(doubleTerminal.getText());
        }
        EasyScriptParser.Bool_literalContext boolLiteral = literalExpr.literal().bool_literal();
        if (boolLiteral != null) {
            return new BoolLiteralExprNode("true".equals(boolLiteral.getText()));
        }
        EasyScriptParser.String_literalContext stringTerminal = literalExpr.literal().string_literal();
        if (stringTerminal != null) {
            String stringLiteral = stringTerminal.getText();
            // remove the quotes delineating the string literal,
            // and unescape the string (meaning, turn \' into ', etc.)
            return new StringLiteralExprNode(StringEscapeUtils.unescapeJson(
                    stringLiteral.substring(1, stringLiteral.length() - 1)));
        }
        return new UndefinedLiteralExprNode();
    }

    private EasyScriptExprNode parseReference(String variableId) {
        FrameMember frameMember = this.findFrameMember(variableId);
        if (frameMember == null) {
            // we know for sure this is a reference to a global variable
            return GlobalVarReferenceExprNodeGen.create(GlobalScopeObjectExprNodeGen.create(), variableId);
        } else {
            return frameMember instanceof FunctionArgument
                    // an int means this is a function parameter
                    ? new ReadFunctionArgExprNode(((FunctionArgument) frameMember).argumentIndex)
                    // this means this is a local variable
                    : LocalVarReferenceExprNodeGen.create(((LocalVariable) frameMember).variableIndex);
        }
    }

    private EasyScriptExprNode parsePropertyReadExpr(EasyScriptParser.PropertyReadExpr5Context propertyReadExpr) {
        return PropertyReadExprNodeGen.create(
                this.parseExpr5(propertyReadExpr.expr5()),
                propertyReadExpr.ID().getText());
    }

    private ArrayLiteralExprNode parseArrayLiteralExpr(EasyScriptParser.ArrayLiteralExpr5Context arrayLiteralExpr) {
        return new ArrayLiteralExprNode(this.arrayShape, arrayLiteralExpr.expr1().stream()
                .map(arrayElExpr -> this.parseExpr1(arrayElExpr))
                .collect(Collectors.toList()));
    }

    private EasyScriptExprNode parseArrayIndexReadExpr(EasyScriptParser.ArrayIndexReadExpr5Context arrayIndexReadExpr) {
        return ArrayIndexReadExprNodeGen.create(
                this.parseExpr5(arrayIndexReadExpr.arr),
                this.parseExpr1(arrayIndexReadExpr.index));
    }

    private FunctionCallExprNode parseCallExpr(EasyScriptParser.CallExpr5Context callExpr) {
        return new FunctionCallExprNode(
                parseExpr5(callExpr.expr5()),
                callExpr.expr1().stream()
                        .map(this::parseExpr1)
                        .collect(Collectors.toList()));
    }

    private EasyScriptExprNode parseIntLiteral(String text) {
        try {
            return new IntLiteralExprNode(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            // it's possible that the integer literal is too big to fit in a 32-bit Java `int` -
            // in that case, fall back to a double literal
            return parseDoubleLiteral(text);
        }
    }

    private DoubleLiteralExprNode parseDoubleLiteral(String text) {
        return new DoubleLiteralExprNode(Double.parseDouble(text));
    }

    private FrameMember findFrameMember(String memberName) {
        for (var scope : this.localScopes) {
            FrameMember ret = scope.get(memberName);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }
}
