package com.endoflineblog.truffle.part_08;

import com.endoflineblog.truffle.part_08.nodes.exprs.AdditionExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.BoolLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.GlobalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.IntLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.LocalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.LocalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.NegationExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.NegationExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.EqualityExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.comparisons.InequalityExprNodeGen;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.WriteFunctionArgExprNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.FuncDeclStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.GlobalVarDeclStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.LocalVarDeclStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.ReturnStmtNode;
import com.endoflineblog.truffle.part_08.nodes.stmts.UserFuncBlockStmtNode;
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
    public static List<EasyScriptStmtNode> parse(Reader program) throws IOException {
        var lexer = new EasyScriptLexer(new ANTLRInputStream(program));
        // remove the default console error listener
        lexer.removeErrorListeners();
        var parser = new EasyScriptParser(new CommonTokenStream(lexer));
        // remove the default console error listener
        parser.removeErrorListeners();
        // throw an exception when a parsing error is encountered
        parser.setErrorHandler(new BailErrorStrategy());
        return new EasyScriptTruffleParser().parseStmtsList(parser.start().stmt());
    }

    /**
     * Map containing bindings for the function arguments and local variables when parsing function definitions.
     * Function arguments will be mapped to integer indexes, starting at 0,
     * while local variables of functions will be mapped to their {@link FrameSlot}s.
     */
    private final Map<String, Object> functionLocals;

    /**
     * The {@link FrameDescriptor} for a given function definition.
     * This field is non-null only if we are parsing a function definition.
     */
    private FrameDescriptor frameDescriptor;

    private EasyScriptTruffleParser() {
        this.functionLocals = new HashMap<>();
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
                    if (this.frameDescriptor == null) {
                        // this is a global variable
                        varDecls.add(new GlobalVarDeclStmtNode(variableId, declarationKind));
                    } else {
                        // this is a function-local variable
                        FrameSlot frameSlot;
                        try {
                            frameSlot = this.frameDescriptor.addFrameSlot(variableId, declarationKind, FrameSlotKind.Object);
                        } catch (IllegalArgumentException e) {
                            throw new EasyScriptException("Identifier '" + variableId + "' has already been declared");
                        }
                        if (this.functionLocals.put(variableId, frameSlot) != null) {
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
                    EasyScriptExprNode assignmentExpr = this.frameDescriptor == null
                            ? GlobalVarAssignmentExprNodeGen.create(initializerExpr, variableId)
                            :  LocalVarAssignmentExprNodeGen.create(initializerExpr,
                                    this.frameDescriptor.findFrameSlot(variableId));
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
        if (this.frameDescriptor == null) {
            throw new EasyScriptException("return statement is not allowed outside functions");
        }
        return new ReturnStmtNode(returnStmt.expr1() == null
                ? new UndefinedLiteralExprNode()
                : this.parseExpr1(returnStmt.expr1()));
    }

    private FuncDeclStmtNode parseFuncDeclStmt(EasyScriptParser.FuncDeclStmtContext funcDeclStmt) {
        if (this.frameDescriptor != null) {
            // we do not allow nested functions (yet 😉)
            throw new EasyScriptException("nested functions are not supported in EasyScript yet");
        }

        // add each parameter to the map, with the correct index
        List<TerminalNode> funcArgs = funcDeclStmt.args.ID();
        int argumentCount = funcArgs.size();
        // create the FrameDescriptor to store each local variable we've seen
        // first, initialize the locals with function arguments
        for (int i = 0; i < argumentCount; i++) {
            this.functionLocals.put(funcArgs.get(i).getText(), i);
        }

        this.frameDescriptor = new FrameDescriptor();
        // parse the statements in the function definition
        List<EasyScriptStmtNode> funcStmts = this.parseStmtsList(funcDeclStmt.stmt());

        FrameDescriptor frameDescriptor = this.frameDescriptor;
        // finally, clear the map of the function locals,
        // in case the program has more than one function inside it
        this.functionLocals.clear();
        this.frameDescriptor = null;

        return new FuncDeclStmtNode(funcDeclStmt.name.getText(),
                frameDescriptor, new UserFuncBlockStmtNode(funcStmts), argumentCount);
    }

    private EasyScriptExprNode parseExpr1(EasyScriptParser.Expr1Context expr1) {
        return expr1 instanceof EasyScriptParser.AssignmentExpr1Context
                ? parseAssignmentExpr((EasyScriptParser.AssignmentExpr1Context) expr1)
                : parseExpr2(((EasyScriptParser.PrecedenceTwoExpr1Context) expr1).expr2());
    }

    private EasyScriptExprNode parseAssignmentExpr(EasyScriptParser.AssignmentExpr1Context assignmentExpr) {
        String variableId = assignmentExpr.ID().getText();
        Object paramIndexOrFrameSlot = this.functionLocals.get(variableId);
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
        if (expr2 instanceof EasyScriptParser.EqNotEqExpr2Context) {
            return this.parseEqNotEqExpression(((EasyScriptParser.EqNotEqExpr2Context) expr2));
        } else {
            return this.parseExpr3(((EasyScriptParser.PrecedenceThreeExpr2Context) expr2).expr3());
        }
    }

    private EasyScriptExprNode parseEqNotEqExpression(EasyScriptParser.EqNotEqExpr2Context eqNotEqExpr) {
        EasyScriptExprNode leftSide = this.parseExpr2(eqNotEqExpr.expr2());
        EasyScriptExprNode rightSide = this.parseExpr3(eqNotEqExpr.expr3());
        return "===".equals(eqNotEqExpr.c.getText())
                ? EqualityExprNodeGen.create(leftSide, rightSide)
                : InequalityExprNodeGen.create(leftSide, rightSide);
    }

    private EasyScriptExprNode parseExpr3(EasyScriptParser.Expr3Context expr3) {
        return this.parseExpr4(((EasyScriptParser.PrecedenceFourExpr3Context) expr3).expr4());
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
        Object paramIndexOrFrameSlot = this.functionLocals.get(variableId);
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
}
