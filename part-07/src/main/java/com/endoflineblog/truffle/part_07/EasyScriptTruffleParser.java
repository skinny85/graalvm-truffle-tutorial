package com.endoflineblog.truffle.part_07;

import com.endoflineblog.truffle.part_07.nodes.exprs.AdditionExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarAssignmentExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarReferenceExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.IntLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.NegationExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.NegationExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.GlobalVarDeclStmtNodeGen;
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
        return new EasyScriptTruffleParser().parseStmtList(parser.start().stmt());
    }

    /**
     * Map containing bindings for the function arguments and local variables when parsing function definitions.
     * Function arguments will be mapped to integer indexes, starting at 0,
     * while local variables of functions will be mapped to their String names.
     */
    private final Map<String, Object> functionLocals = new HashMap<>();

    private List<EasyScriptStmtNode> parseStmtList(List<EasyScriptParser.StmtContext> stmts) {
        var result = new ArrayList<EasyScriptStmtNode>(stmts.size());
        for (EasyScriptParser.StmtContext stmt : stmts) {
            if (stmt instanceof EasyScriptParser.ExprStmtContext) {
                result.add(parseExprStmt((EasyScriptParser.ExprStmtContext) stmt));
            } else if (stmt instanceof EasyScriptParser.FuncDeclStmtContext) {
                    result.add(parseFuncDeclStmt((EasyScriptParser.FuncDeclStmtContext) stmt));
            } else {
                result.addAll(this.parseVarDeclStmt((EasyScriptParser.VarDeclStmtContext) stmt));
            }
        }
        return result;
    }

    private ExprStmtNode parseExprStmt(EasyScriptParser.ExprStmtContext exprStmt) {
        return new ExprStmtNode(parseExpr1(exprStmt.expr1()));
    }

    private EasyScriptStmtNode parseFuncDeclStmt(EasyScriptParser.FuncDeclStmtContext funcDeclStmt) {
        throw new UnsupportedOperationException();
    }

    private List<EasyScriptStmtNode> parseVarDeclStmt(EasyScriptParser.VarDeclStmtContext varDeclStmt) {
        List<EasyScriptParser.BindingContext> varDeclBindings = varDeclStmt.binding();
        var result = new ArrayList<EasyScriptStmtNode>(varDeclBindings.size());
        DeclarationKind declarationKind = DeclarationKind.fromToken(varDeclStmt.kind.getText());
        for (EasyScriptParser.BindingContext varBinding : varDeclBindings) {
            String variableId = varBinding.ID().getText();
            var bindingExpr = varBinding.expr1();
            EasyScriptExprNode initializerExpr;
            if (bindingExpr == null) {
                if (declarationKind == DeclarationKind.CONST) {
                    throw new EasyScriptException("Missing initializer in const declaration '" + variableId + "'");
                }
                initializerExpr = new UndefinedLiteralExprNode();
            } else {
                initializerExpr = parseExpr1(bindingExpr);
            }
            result.add(GlobalVarDeclStmtNodeGen.create(initializerExpr, variableId, declarationKind));
        }
        return result;
    }

    private EasyScriptExprNode parseExpr1(EasyScriptParser.Expr1Context expr1) {
        return expr1 instanceof EasyScriptParser.AssignmentExpr1Context
                ? parseAssignmentExpr((EasyScriptParser.AssignmentExpr1Context) expr1)
                : parseExpr2(((EasyScriptParser.PrecedenceTwoExpr1Context) expr1).expr2());
    }

    private GlobalVarAssignmentExprNode parseAssignmentExpr(EasyScriptParser.AssignmentExpr1Context assignmentExpr) {
        String variableId = assignmentExpr.ID().getText();
        return GlobalVarAssignmentExprNodeGen.create(parseExpr1(assignmentExpr.expr1()), variableId);
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

    private GlobalVarReferenceExprNode parseReference(String variableId) {
        return GlobalVarReferenceExprNodeGen.create(variableId);
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
