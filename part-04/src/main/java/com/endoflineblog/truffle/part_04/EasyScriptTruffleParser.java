package com.endoflineblog.truffle.part_04;

import com.endoflineblog.truffle.part_03.AdditionNode;
import com.endoflineblog.truffle.part_03.AdditionNodeGen;
import com.endoflineblog.truffle.part_03.DoubleLiteralNode;
import com.endoflineblog.truffle.part_03.EasyScriptNode;
import com.endoflineblog.truffle.part_03.IntLiteralNode;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class EasyScriptTruffleParser {
    public static EasyScriptNode parse(String program) {
        var inputStream = new ANTLRInputStream(program);
        var lexer = new EasyScriptLexer(inputStream);
        // remove the default console error listener
        lexer.removeErrorListeners();
        var parser = new EasyScriptParser(new CommonTokenStream(lexer));
        // remove the default console error listener
        parser.removeErrorListeners();
        // throw an exception when a parsing error is encountered
        parser.setErrorHandler(new BailErrorStrategy());
        EasyScriptParser.ExprContext context = parser.start().expr();
        return expr2TruffleNode(context);
    }

    private static EasyScriptNode expr2TruffleNode(EasyScriptParser.ExprContext expr) {
        return expr instanceof EasyScriptParser.AddExprContext
                ? addExpr2AdditionNode((EasyScriptParser.AddExprContext) expr)
                : literalExpr2ExprNode((EasyScriptParser.LiteralExprContext) expr);
    }

    private static AdditionNode addExpr2AdditionNode(EasyScriptParser.AddExprContext addExpr) {
        return AdditionNodeGen.create(
                expr2TruffleNode(addExpr.left),
                expr2TruffleNode(addExpr.right)
        );
    }

    private static EasyScriptNode literalExpr2ExprNode(EasyScriptParser.LiteralExprContext literalExpr) {
        TerminalNode intTerminal = literalExpr.literal().INT();
        return intTerminal != null
                ? new IntLiteralNode(Integer.parseInt(intTerminal.getText()))
                : new DoubleLiteralNode(Double.parseDouble(literalExpr.getText()));
    }
}
