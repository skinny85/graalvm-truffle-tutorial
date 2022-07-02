package com.endoflineblog.truffle.part_08.parsing;

import com.endoflineblog.truffle.part_08.DeclarationKind;
import com.endoflineblog.truffle.part_08.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_08.LocalVariableFrameSlotId;
import com.endoflineblog.truffle.part_08.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.arithmetic.AdditionExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.arithmetic.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.arithmetic.NegationExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.arithmetic.NegationExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.EqualityExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.GreaterExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.GreaterOrEqualExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.InequalityExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.LesserExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.LesserOrEqualExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.WriteFunctionArgExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.literals.BoolLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.literals.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.literals.IntLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.literals.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.variables.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.variables.GlobalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.variables.LocalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.variables.LocalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.controlflow.BreakStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.controlflow.ContinueStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.controlflow.IfStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.controlflow.ReturnStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.loops.DoWhileStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.loops.ForStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.loops.WhileStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.variables.FuncDeclStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.variables.GlobalVarDeclStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.variables.LocalVarDeclStmtNode;
import com.endoflineblog.truffle.part_08.parsing.antlr.EasyScriptLexer;
import com.endoflineblog.truffle.part_08.parsing.antlr.EasyScriptParser;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

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
 * with the grammar defined in the src/main/antlr/com/endoflineblog/truffle/part_07/EasyScript.g4 file.
 * This class is invoked by the {@link EasyScriptTruffleLanguage TruffleLanguage implementation for this part}.
 *
 * @see #parse
 */
public final class EasyScriptTruffleParser {
    public static ParsingResult parse(Reader program) throws IOException {
        var lexer = new EasyScriptLexer(new ANTLRInputStream(program));
        // remove the default console error listener
        lexer.removeErrorListeners();
        var parser = new EasyScriptParser(new CommonTokenStream(lexer));
        // remove the default console error listener
        parser.removeErrorListeners();
        // throw an exception when a parsing error is encountered
        parser.setErrorHandler(new BailErrorStrategy());
        var easyScriptTruffleParser = new EasyScriptTruffleParser();
        List<EasyScriptStmtNode> stmts = easyScriptTruffleParser.parseStmtsList(parser.start().stmt());
        return new ParsingResult(
                new BlockStmtNode(stmts),
                easyScriptTruffleParser.frameDescriptor);
    }

    private enum ParserState { TOP_LEVEL, NESTED_SCOPE_IN_TOP_LEVEL, FUNC_DEF }

    /** Whether we're parsing a function definition. */
    private ParserState state;

    /**
     * The {@link FrameDescriptor} for either a given function definition,
     * or for any local variables in the statements of the top-level program.
     */
    private FrameDescriptor frameDescriptor;

    /**
     * Map containing bindings for the function arguments and local variables when parsing function definitions
     * and nested scopes of the top-level object.
     * Function arguments will be mapped to integer indexes, starting at 0,
     * while local variables of functions will be mapped to their {@link FrameSlot}s.
     */
    private Stack<Map<String, Object>> localScopes;

    /**
     * The counter that makes it easy to generate unique variable names for local variables
     * (as their names can repeat in nested scopes).
     */
    private int localVariablesCounter;

    private EasyScriptTruffleParser() {
        this.state = ParserState.TOP_LEVEL;
        this.frameDescriptor = new FrameDescriptor();
        this.localScopes = new Stack<>();
        this.localVariablesCounter = 0;
    }

    private List<EasyScriptStmtNode> parseStmtsList(List<EasyScriptParser.StmtContext> stmts) {
        // implement hoisting of declarations
        // see: https://developer.mozilla.org/en-US/docs/Glossary/Hoisting

        // in the first pass, only handle declarations
        var funcDecls = new ArrayList<FuncDeclStmtNode>();
        var varDecls = new ArrayList<EasyScriptStmtNode>();
        for (EasyScriptParser.StmtContext stmt : stmts) {
            if (stmt instanceof EasyScriptParser.FuncDeclStmtContext) {
                funcDecls.add(this.parseFuncDeclStmt((EasyScriptParser.FuncDeclStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.VarDeclStmtContext) {
                EasyScriptParser.VarDeclStmtContext varDeclStmt = (EasyScriptParser.VarDeclStmtContext) stmt;
                List<EasyScriptParser.BindingContext> varDeclBindings = varDeclStmt.binding();
                DeclarationKind declarationKind = DeclarationKind.fromToken(varDeclStmt.kind.getText());
                for (EasyScriptParser.BindingContext varBinding : varDeclBindings) {
                    String variableId = varBinding.ID().getText();
                    if (this.state == ParserState.TOP_LEVEL) {
                        // this is a global variable
                        varDecls.add(new GlobalVarDeclStmtNode(variableId, declarationKind));
                    } else {
                        // this is a local variable (either of a function, or on the top-level)
                        var frameSlotId = new LocalVariableFrameSlotId(variableId, ++this.localVariablesCounter);
                        FrameSlot frameSlot = this.frameDescriptor.addFrameSlot(frameSlotId, declarationKind, FrameSlotKind.Object);
                        if (this.localScopes.peek().putIfAbsent(variableId, frameSlot) != null) {
                            throw new EasyScriptException("Identifier '" + variableId + "' has already been declared");
                        }
                        varDecls.add(new LocalVarDeclStmtNode(frameSlot));
                    }
                }
            }
        }

        // in the second pass, only handle expression statements
        // (and add new expression statements that represent the initializer of the variable declarations)
        var exprStmts = new ArrayList<EasyScriptStmtNode>();
        for (EasyScriptParser.StmtContext stmt : stmts) {
            if (stmt instanceof EasyScriptParser.ExprStmtContext) {
                exprStmts.add(this.parseExprStmt((EasyScriptParser.ExprStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.ReturnStmtContext) {
                exprStmts.add(this.parseReturnStmt((EasyScriptParser.ReturnStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.IfStmtContext) {
                exprStmts.add(this.parseIfStmt((EasyScriptParser.IfStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.WhileStmtContext) {
                exprStmts.add(this.parseWhileStmt((EasyScriptParser.WhileStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.DoWhileStmtContext) {
                exprStmts.add(this.parseDoWhileStmt((EasyScriptParser.DoWhileStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.ForStmtContext) {
                exprStmts.add(this.parseForStmt((EasyScriptParser.ForStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.BlockStmtContext) {
                exprStmts.add(this.parseStmtBlock((EasyScriptParser.BlockStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.BreakStmtContext) {
                exprStmts.add(new BreakStmtNode());
            } else if (stmt instanceof EasyScriptParser.ContinueStmtContext) {
                exprStmts.add(new ContinueStmtNode());
            } else if (stmt instanceof EasyScriptParser.VarDeclStmtContext) {
                // we turn the variable declaration into an assignment expression
                EasyScriptParser.VarDeclStmtContext varDeclStmt = (EasyScriptParser.VarDeclStmtContext) stmt;
                List<EasyScriptParser.BindingContext> varDeclBindings = varDeclStmt.binding();
                DeclarationKind declarationKind = DeclarationKind.fromToken(varDeclStmt.kind.getText());
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
                    EasyScriptExprNode assignmentExpr = this.state == ParserState.TOP_LEVEL
                            ? GlobalVarAssignmentExprNodeGen.create(initializerExpr, variableId)
                            :  LocalVarAssignmentExprNodeGen.create(initializerExpr,
                                (FrameSlot) this.localScopes.peek().get(variableId));
                    exprStmts.add(new ExprStmtNode(assignmentExpr, /* discardExpressionValue */ true));
                }
            }
        }

        // the final result is: the function declarations first,
        // then the variable declarations (initialized with default values),
        // and then finally the expression statements
        // (including the variable initializers turned into assignment expressions)
        var result = new ArrayList<EasyScriptStmtNode>(funcDecls.size() + varDecls.size() + exprStmts.size());
        result.addAll(funcDecls);
        result.addAll(varDecls);
        result.addAll(exprStmts);
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
        FrameDescriptor previousFrameDescriptor = this.frameDescriptor;
        ParserState previousParserState = this.state;
        var previousLocalScopes = this.localScopes;

        // initialize the new state
        this.frameDescriptor = new FrameDescriptor();
        this.state = ParserState.FUNC_DEF;
        this.localScopes = new Stack<>();

        var localVariables = new HashMap<String, Object>();
        // add each parameter to the map, with the correct index
        List<TerminalNode> funcArgs = funcDeclStmt.args.ID();
        int argumentCount = funcArgs.size();
        // first, initialize the locals with function arguments
        for (int i = 0; i < argumentCount; i++) {
            localVariables.put(funcArgs.get(i).getText(), i);
        }
        this.localScopes.push(localVariables);

        // parse the statements in the function definition
        List<EasyScriptStmtNode> funcStmts = this.parseStmtsList(funcDeclStmt.stmt());

        FrameDescriptor frameDescriptor = this.frameDescriptor;
        // bring back the old state
        this.frameDescriptor = previousFrameDescriptor;
        this.state = previousParserState;
        this.localScopes = previousLocalScopes;

        return new FuncDeclStmtNode(funcDeclStmt.name.getText(),
                frameDescriptor, new UserFuncBodyStmtNode(funcStmts), argumentCount);
    }

    private EasyScriptExprNode parseExpr1(EasyScriptParser.Expr1Context expr1) {
        if (expr1 == null) {
            return null;
        }
        return expr1 instanceof EasyScriptParser.AssignmentExpr1Context
                ? parseAssignmentExpr((EasyScriptParser.AssignmentExpr1Context) expr1)
                : parseExpr2(((EasyScriptParser.PrecedenceTwoExpr1Context) expr1).expr2());
    }

    private EasyScriptExprNode parseAssignmentExpr(EasyScriptParser.AssignmentExpr1Context assignmentExpr) {
        String variableId = assignmentExpr.ID().getText();
        Object paramIndexOrFrameSlot = this.findLocalVariable(variableId);
        EasyScriptExprNode initializerExpr = this.parseExpr1(assignmentExpr.expr1());
        if (paramIndexOrFrameSlot == null) {
            return GlobalVarAssignmentExprNodeGen.create(initializerExpr, variableId);
        } else {
            if (paramIndexOrFrameSlot instanceof Integer) {
                return new WriteFunctionArgExprNode((Integer) paramIndexOrFrameSlot, initializerExpr);
            } else {
                FrameSlot frameSlot = (FrameSlot) paramIndexOrFrameSlot;
                if (frameSlot.getInfo() == DeclarationKind.CONST) {
                    throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
                }
                return LocalVarAssignmentExprNodeGen.create(initializerExpr, frameSlot);
            }
        }
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
        if (expr4 instanceof EasyScriptParser.AddExpr4Context) {
            return parseAdditionExpr((EasyScriptParser.AddExpr4Context) expr4);
        } else if (expr4 instanceof EasyScriptParser.UnaryMinusExpr4Context) {
            return parseUnaryMinusExpr((EasyScriptParser.UnaryMinusExpr4Context) expr4);
        } else {
            return parseExpr5(((EasyScriptParser.PrecedenceFiveExpr4Context) expr4).expr5());
        }
    }

    private AdditionExprNode parseAdditionExpr(EasyScriptParser.AddExpr4Context addExpr) {
        return AdditionExprNodeGen.create(
                parseExpr4(addExpr.left),
                parseExpr5(addExpr.right));
    }

    private NegationExprNode parseUnaryMinusExpr(EasyScriptParser.UnaryMinusExpr4Context unaryMinusExpr) {
        return NegationExprNodeGen.create(parseExpr5(unaryMinusExpr.expr5()));
    }

    private EasyScriptExprNode parseExpr5(EasyScriptParser.Expr5Context expr5) {
        if (expr5 instanceof EasyScriptParser.LiteralExpr5Context) {
            return parseLiteralExpr((EasyScriptParser.LiteralExpr5Context) expr5);
        } else if (expr5 instanceof EasyScriptParser.SimpleReferenceExpr5Context) {
            return parseReference(((EasyScriptParser.SimpleReferenceExpr5Context) expr5).ID().getText());
        } else if (expr5 instanceof EasyScriptParser.ComplexReferenceExpr5Context) {
            var complexRef = (EasyScriptParser.ComplexReferenceExpr5Context) expr5;
            return parseReference(complexRef.ID().stream()
                    .map(id -> id.getText())
                    .collect(Collectors.joining(".")));
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
        return new UndefinedLiteralExprNode();
    }

    private EasyScriptExprNode parseReference(String variableId) {
        Object paramIndexOrFrameSlot = this.findLocalVariable(variableId);
        if (paramIndexOrFrameSlot == null) {
            // we know for sure this is a reference to a global variable
            return GlobalVarReferenceExprNodeGen.create(variableId);
        } else {
            return paramIndexOrFrameSlot instanceof Integer
                    // an int means this is a function parameter
                    ? new ReadFunctionArgExprNode((Integer) paramIndexOrFrameSlot)
                    // this means this is a local variable
                    : LocalVarReferenceExprNodeGen.create((FrameSlot) paramIndexOrFrameSlot);
        }
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

    private Object findLocalVariable(String variableId) {
        for (var scope : this.localScopes) {
            Object ret = scope.get(variableId);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }
}
