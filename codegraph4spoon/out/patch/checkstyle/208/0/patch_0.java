public void testSemantic() throws Exception {
    mConfig.setJavadocScope(NOTHING);
    mConfig.setTryBlock(STMT);
    mConfig.setCatchBlock(STMT);
    mConfig.setFinallyBlock(STMT);
    mConfig.setBooleanProperty(IGNORE_IMPORTS_PROP, null);
    mConfig.setBooleanFlag(IGNORE_LONG_ELL_PROP, false);
    mConfig.setIllegalInstantiations((("java.lang.Boolean," + "com.puppycrawl.tools.checkstyle.InputModifier,") + "java.io.File,") + "java.awt.Color");
    final Checker c = createChecker();
    final String filepath = getPath("InputSemantic.java");
    assertNotNull(c);
    final String[] expected = new String[]{ filepath + ":19:21: Avoid instantiation of java.lang.Boolean", filepath + ":24:21: Avoid instantiation of java.lang.Boolean", filepath + ":30:16: Avoid instantiation of java.lang.Boolean", (filepath + ":37:21: Avoid instantiation of ") + "com.puppycrawl.tools.checkstyle.InputModifier", filepath + ":40:18: Avoid instantiation of java.io.File", filepath + ":43:21: Avoid instantiation of java.awt.Color", filepath + ":51:65: Must have at least one statement.", filepath + ":53:41: Must have at least one statement.", filepath + ":70:38: Must have at least one statement.", filepath + ":71:52: Must have at least one statement.", filepath + ":72:45: Must have at least one statement.", filepath + ":74:13: Must have at least one statement.", filepath + ":76:17: Must have at least one statement.", filepath + ":78:13: Must have at least one statement.", filepath + ":81:17: Must have at least one statement.", filepath + ":93:43: Should use uppercase 'L'." };
    verify(c, filepath, expected);
}