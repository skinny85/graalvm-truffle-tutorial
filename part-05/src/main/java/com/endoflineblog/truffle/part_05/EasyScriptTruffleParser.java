package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.nodes.exprs.AdditionExprNode;
import com.endoflineblog.truffle.part_05.nodes.exprs.AdditionExprNodeGen;
import com.endoflineblog.truffle.part_05.nodes.exprs.AssignmentExprNode;
import com.endoflineblog.truffle.part_05.nodes.exprs.AssignmentExprNodeGen;
import com.endoflineblog.truffle.part_05.nodes.exprs.DoubleLiteralExprNode;
import com.endoflineblog.truffle.part_05.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_05.nodes.exprs.IntLiteralExprNode;
import com.endoflineblog.truffle.part_05.nodes.exprs.ReferenceExprNode;
import com.endoflineblog.truffle.part_05.nodes.exprs.ReferenceExprNodeGen;
import com.endoflineblog.truffle.part_05.nodes.stmts.DeclStmtNode;
import com.endoflineblog.truffle.part_05.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_05.nodes.stmts.ExprStmtNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
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
    private final FrameDescriptor frameDescriptor;

    public EasyScriptTruffleParser(FrameDescriptor frameDescriptor) {
        this.frameDescriptor = frameDescriptor;
    }

    public List<EasyScriptStmtNode> parse(Reader program) throws IOException {
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

    private List<EasyScriptStmtNode> parseStmtList(List<EasyScriptParser.StmtContext> stmts) {
        return stmts.stream()
                .flatMap(stmt -> stmt instanceof EasyScriptParser.ExprStmtContext
                        ? Stream.of(this.parseExprStmt((EasyScriptParser.ExprStmtContext) stmt))
                        : this.parseDeclStmt((EasyScriptParser.DeclStmtContext) stmt))
                .collect(Collectors.toList());
    }

    private ExprStmtNode parseExprStmt(EasyScriptParser.ExprStmtContext exprStmt) {
        return new ExprStmtNode(this.parseExpr1(exprStmt.expr1()));
    }

    enum VariableMutability { MUTABLE, IMMUTABLE }

    private Stream<EasyScriptStmtNode> parseDeclStmt(EasyScriptParser.DeclStmtContext declStmt) {
        var variableMutability = declStmt.getText().startsWith("const")
                ? VariableMutability.IMMUTABLE
                : VariableMutability.MUTABLE;
        return declStmt.binding()
                .stream()
                .map(binding -> {
                    // create a new frame slot for this variable
                    String variableId = binding.ID().getText();
                    FrameSlot frameSlot;
                    try {
                        frameSlot = this.frameDescriptor.addFrameSlot(variableId, variableMutability, FrameSlotKind.Illegal);
                    } catch (IllegalArgumentException e) {
                        throw new EasyScriptException("Identifier '" + variableId + "' has already been declared");
                    }
                    return new DeclStmtNode(AssignmentExprNodeGen.create(
                            this.parseExpr1(binding.expr1()), frameSlot));
                });
    }

    private EasyScriptExprNode parseExpr1(EasyScriptParser.Expr1Context expr1) {
        if (expr1 instanceof EasyScriptParser.AssignmentExpr1Context) {
            return parseAssignmentExpr((EasyScriptParser.AssignmentExpr1Context) expr1);
        } else {
            return this.parseExpr2(((EasyScriptParser.PrecedenceTwoExpr1Context) expr1).expr2());
        }
    }

    private AssignmentExprNode parseAssignmentExpr(EasyScriptParser.AssignmentExpr1Context assignmentExpr) {
        EasyScriptParser.BindingContext binding = assignmentExpr.binding();
        String variableId = binding.ID().getText();
        // retrieve the frame slot for this variable
        FrameSlot frameSlot = this.frameDescriptor.findFrameSlot(variableId);
        if (frameSlot == null) {
            throw new EasyScriptException("'" + variableId + "' is not defined");
        }
        if (frameSlot.getInfo() == VariableMutability.IMMUTABLE) {
            throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
        }
        return AssignmentExprNodeGen.create(this.parseExpr1(binding.expr1()), frameSlot);
    }

    private EasyScriptExprNode parseExpr2(EasyScriptParser.Expr2Context expr2) {
        return expr2 instanceof EasyScriptParser.AddExpr2Context
                ? this.parseAdditionExpr((EasyScriptParser.AddExpr2Context) expr2)
                : this.parseExpr3(((EasyScriptParser.PrecedenceThreeExpr2Context) expr2).expr3());
    }

    private AdditionExprNode parseAdditionExpr(EasyScriptParser.AddExpr2Context addExpr) {
        return AdditionExprNodeGen.create(
                this.parseExpr2(addExpr.left),
                this.parseExpr3(addExpr.right));
    }

    private EasyScriptExprNode parseExpr3(EasyScriptParser.Expr3Context expr3) {
        if (expr3 instanceof EasyScriptParser.LiteralExpr3Context) {
            return this.parseLiteralExpr((EasyScriptParser.LiteralExpr3Context) expr3);
        } else if (expr3 instanceof EasyScriptParser.ReferenceExpr3Context) {
            return this.parseReferenceExpr((EasyScriptParser.ReferenceExpr3Context) expr3);
        } else {
            return this.parseExpr1(((EasyScriptParser.PrecedenceOneExpr3Context) expr3).expr1());
        }
    }

    private EasyScriptExprNode parseLiteralExpr(EasyScriptParser.LiteralExpr3Context literalExpr) {
        TerminalNode intTerminal = literalExpr.literal().INT();
        return intTerminal != null
                ? new IntLiteralExprNode(Integer.parseInt(intTerminal.getText()))
                : new DoubleLiteralExprNode(Double.parseDouble(literalExpr.getText()));
    }

    private ReferenceExprNode parseReferenceExpr(EasyScriptParser.ReferenceExpr3Context refExpr) {
        String variableId = refExpr.ID().getText();
        // retrieve the frame slot for this variable
        FrameSlot frameSlot = this.frameDescriptor.findFrameSlot(variableId);
        if (frameSlot == null) {
            throw new EasyScriptException("'" + variableId + "' is not defined");
        }
        return ReferenceExprNodeGen.create(frameSlot);
    }
}
