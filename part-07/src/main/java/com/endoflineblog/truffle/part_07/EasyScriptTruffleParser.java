package com.endoflineblog.truffle.part_07;

import com.endoflineblog.truffle.part_07.nodes.exprs.AdditionExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.IntLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.LocalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.LocalVarReferenceExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.NegationExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.NegationExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.WriteFunctionArgExprNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.BlockStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.FuncDeclStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.GlobalVarDeclStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.LocalVarDeclStmtNode;
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
     * while local variables of functions will be mapped to their String names.
     * This field is non-null only if we are parsing a function definition.
     */
    private final Map<String, Object> functionLocals;
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
        var exprStmts = new ArrayList<ExprStmtNode>();
        for (EasyScriptParser.StmtContext stmt : stmts) {
            if (stmt instanceof EasyScriptParser.ExprStmtContext) {
                exprStmts.add(this.parseExprStmt((EasyScriptParser.ExprStmtContext) stmt));
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
                                    this.frameDescriptor.findFrameSlot(variableId), /* initializingAssignment */ true);
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

    private FuncDeclStmtNode parseFuncDeclStmt(EasyScriptParser.FuncDeclStmtContext funcDeclStmt) {
        if (this.frameDescriptor != null) {
            // we do not allow nested functions (yet 😉)
            throw new EasyScriptException("nested functions are not supported in EasyScript yet");
        }

        // add each parameter to the map, with the correct index
        List<TerminalNode> funcArgs = funcDeclStmt.args.ID();
        int argumentCount = funcArgs.size();
        // create the FrameDescriptor to store each local variable we've seen
        this.frameDescriptor = new FrameDescriptor();
        // first, initialize the locals with function arguments
        for (int i = 0; i < argumentCount; i++) {
            this.functionLocals.put(funcArgs.get(i).getText(), i);
        }

        // parse the statements in the function definition
        List<EasyScriptStmtNode> funcStmts = this.parseStmtsList(funcDeclStmt.stmt());
        FrameDescriptor frameDescriptor = this.frameDescriptor;

        // finally, clear the map of the function locals,
        // in case the program has more than one function inside it
        this.functionLocals.clear();
        this.frameDescriptor = null;

        return new FuncDeclStmtNode(funcDeclStmt.name.getText(),
                frameDescriptor, new BlockStmtNode(funcStmts), argumentCount);
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
        return paramIndexOrFrameSlot == null
                ? GlobalVarAssignmentExprNodeGen.create(initializerExpr, variableId)
                : (paramIndexOrFrameSlot instanceof Integer
                    ? new WriteFunctionArgExprNode((Integer) paramIndexOrFrameSlot, initializerExpr)
                    : LocalVarAssignmentExprNodeGen.create(initializerExpr,
                        (FrameSlot) paramIndexOrFrameSlot, /* initializingAssignment */ false));
    }

    private EasyScriptExprNode parseExpr2(EasyScriptParser.Expr2Context expr2) {
        if (expr2 instanceof EasyScriptParser.AddExpr2Context) {
            return parseAdditionExpr((EasyScriptParser.AddExpr2Context) expr2);
        } else if (expr2 instanceof EasyScriptParser.UnaryMinusExpr2Context) {
            return parseUnaryMinusExpr((EasyScriptParser.UnaryMinusExpr2Context) expr2);
        } else {
            return parseExpr3(((EasyScriptParser.PrecedenceThreeExpr2Context) expr2).expr3());
        }
    }

    private AdditionExprNode parseAdditionExpr(EasyScriptParser.AddExpr2Context addExpr) {
        return AdditionExprNodeGen.create(
                parseExpr2(addExpr.left),
                parseExpr3(addExpr.right));
    }

    private NegationExprNode parseUnaryMinusExpr(EasyScriptParser.UnaryMinusExpr2Context unaryMinusExpr) {
        return NegationExprNodeGen.create(parseExpr3(unaryMinusExpr.expr3()));
    }

    private EasyScriptExprNode parseExpr3(EasyScriptParser.Expr3Context expr3) {
        if (expr3 instanceof EasyScriptParser.LiteralExpr3Context) {
            return parseLiteralExpr((EasyScriptParser.LiteralExpr3Context) expr3);
        } else if (expr3 instanceof EasyScriptParser.SimpleReferenceExpr3Context) {
            return parseReference(((EasyScriptParser.SimpleReferenceExpr3Context) expr3).ID().getText());
        } else if (expr3 instanceof EasyScriptParser.ComplexReferenceExpr3Context) {
            var complexRef = (EasyScriptParser.ComplexReferenceExpr3Context) expr3;
            return parseReference(complexRef.ID().stream()
                    .map(id -> id.getText())
                    .collect(Collectors.joining(".")));
        } else if (expr3 instanceof EasyScriptParser.CallExpr3Context) {
            return parseCallExpr((EasyScriptParser.CallExpr3Context) expr3);
        } else {
            return parseExpr1(((EasyScriptParser.PrecedenceOneExpr3Context) expr3).expr1());
        }
    }

    private EasyScriptExprNode parseLiteralExpr(EasyScriptParser.LiteralExpr3Context literalExpr) {
        TerminalNode intTerminal = literalExpr.literal().INT();
        if (intTerminal != null) {
            return parseIntLiteral(intTerminal.getText());
        }
        TerminalNode doubleTerminal = literalExpr.literal().DOUBLE();
        return doubleTerminal != null
                ? parseDoubleLiteral(doubleTerminal.getText())
                : new UndefinedLiteralExprNode();
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
                    : new LocalVarReferenceExprNode((FrameSlot) paramIndexOrFrameSlot);
        }
    }

    private FunctionCallExprNode parseCallExpr(EasyScriptParser.CallExpr3Context callExpr) {
        return new FunctionCallExprNode(
                parseExpr3(callExpr.expr3()),
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
