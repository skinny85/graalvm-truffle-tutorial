package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.nodes.EasyScriptRootNode;
import com.endoflineblog.truffle.part_05.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;

import java.util.List;

/**
 * The EasyScript Graal polyglot language implementation.
 * Very similar to EasyScriptTruffleLanguage in part 4.
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<Void> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        var frameDescriptor = new FrameDescriptor();
        var easyScriptTruffleParser = new EasyScriptTruffleParser(frameDescriptor);
        List<EasyScriptStmtNode> stmts = easyScriptTruffleParser.parse(request.getSource().getReader());
        var rootNode = new EasyScriptRootNode(this, frameDescriptor, stmts);
        return Truffle.getRuntime().createCallTarget(rootNode);
    }

    /**
     * We still don't need a Context,
     * so we still return {@code null} here.
     */
    @Override
    protected Void createContext(Env env) {
        return null;
    }
}
