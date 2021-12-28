module part_04 {
    requires org.graalvm.truffle;
    requires org.graalvm.sdk;
    provides com.oracle.truffle.api.TruffleLanguage.Provider
        with com.endoflineblog.truffle.part_04.EasyScriptTruffleLanguageProvider;
}
