package com.endoflineblog.truffle.part_06;

import com.endoflineblog.truffle.part_06.nodes.exprs.AdditionExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.GlobalVarAssignmentExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.GlobalVarReferenceExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.GlobalVarReferenceExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.IntLiteralExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.NegationExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.NegationExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.FunctionCallExprNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.GlobalVarDeclStmtNodeGen;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is the class that parses the program and turns it into a Truffle AST.
 * It uses ANTLR to perform the actual parsing,
 * with the grammar defined in the src/main/antlr/com/endoflineblog/truffle/part_05/EasyScript.g4 file.
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
        return parseStmtList(parser.start().stmt());
    }

    private static List<EasyScriptStmtNode> parseStmtList(List<EasyScriptParser.StmtContext> stmts) {
        return stmts.stream()
                .flatMap(stmt -> stmt instanceof EasyScriptParser.ExprStmtContext
                        ? Stream.of(parseExprStmt((EasyScriptParser.ExprStmtContext) stmt))
                        : parseDeclStmt((EasyScriptParser.DeclStmtContext) stmt))
                .collect(Collectors.toList());
    }

    private static ExprStmtNode parseExprStmt(EasyScriptParser.ExprStmtContext exprStmt) {
        return new ExprStmtNode(parseExpr1(exprStmt.expr1()));
    }

    private static Stream<EasyScriptStmtNode> parseDeclStmt(EasyScriptParser.DeclStmtContext declStmt) {
        DeclarationKind declarationKind = DeclarationKind.fromToken(declStmt.kind.getText());
        return declStmt.binding()
                .stream()
                .map(binding -> {
                    String variableId = binding.ID().getText();
                    var bindingExpr = binding.expr1();
                    EasyScriptExprNode initializerExpr;
                    if (bindingExpr == null) {
                        if (declarationKind == DeclarationKind.CONST) {
                            throw new EasyScriptException("Missing initializer in const declaration '" + variableId + "'");
                        }
                        initializerExpr = new UndefinedLiteralExprNode();
                    } else {
                        initializerExpr = parseExpr1(bindingExpr);
                    }
                    return GlobalVarDeclStmtNodeGen.create(initializerExpr, variableId, declarationKind);
                });
    }

    private static EasyScriptExprNode parseExpr1(EasyScriptParser.Expr1Context expr1) {
        return expr1 instanceof EasyScriptParser.AssignmentExpr1Context
                ? parseAssignmentExpr((EasyScriptParser.AssignmentExpr1Context) expr1)
                : parseExpr2(((EasyScriptParser.PrecedenceTwoExpr1Context) expr1).expr2());
    }

    private static GlobalVarAssignmentExprNode parseAssignmentExpr(EasyScriptParser.AssignmentExpr1Context assignmentExpr) {
        String variableId = assignmentExpr.ID().getText();
        return GlobalVarAssignmentExprNodeGen.create(parseExpr1(assignmentExpr.expr1()), variableId);
    }

    private static EasyScriptExprNode parseExpr2(EasyScriptParser.Expr2Context expr2) {
        if (expr2 instanceof EasyScriptParser.AddExpr2Context) {
            return parseAdditionExpr((EasyScriptParser.AddExpr2Context) expr2);
        } else if (expr2 instanceof EasyScriptParser.UnaryMinusExpr2Context) {
            return parseUnaryMinusExpr((EasyScriptParser.UnaryMinusExpr2Context) expr2);
        } else {
            return parseExpr3(((EasyScriptParser.PrecedenceThreeExpr2Context) expr2).expr3());
        }
    }

    private static AdditionExprNode parseAdditionExpr(EasyScriptParser.AddExpr2Context addExpr) {
        return AdditionExprNodeGen.create(
                parseExpr2(addExpr.left),
                parseExpr3(addExpr.right));
    }

    private static NegationExprNode parseUnaryMinusExpr(EasyScriptParser.UnaryMinusExpr2Context unaryMinusExpr) {
        return NegationExprNodeGen.create(parseExpr3(unaryMinusExpr.expr3()));
    }

    private static EasyScriptExprNode parseExpr3(EasyScriptParser.Expr3Context expr3) {
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

    private static EasyScriptExprNode parseLiteralExpr(EasyScriptParser.LiteralExpr3Context literalExpr) {
        TerminalNode intTerminal = literalExpr.literal().INT();
        if (intTerminal != null) {
            return new IntLiteralExprNode(Integer.parseInt(intTerminal.getText()));
        }
        TerminalNode doubleTerminal = literalExpr.literal().DOUBLE();
        return doubleTerminal != null
                ? new DoubleLiteralExprNode(Double.parseDouble(doubleTerminal.getText()))
                : new UndefinedLiteralExprNode();
    }

    private static GlobalVarReferenceExprNode parseReference(String variableId) {
        return GlobalVarReferenceExprNodeGen.create(variableId);
    }

    private static FunctionCallExprNode parseCallExpr(EasyScriptParser.CallExpr3Context callExpr) {
        return new FunctionCallExprNode(
                parseExpr3(callExpr.expr3()),
                callExpr.expr1().stream()
                        .map(EasyScriptTruffleParser::parseExpr1)
                        .collect(Collectors.toList()));
    }
}
