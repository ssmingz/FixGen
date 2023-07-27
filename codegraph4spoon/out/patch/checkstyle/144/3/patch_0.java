class PlaceHold{
public void testPackageHtml() throws Exception {
    mConfig.setBooleanFlag(, null);
    mConfig.setJavadocScope(PRIVATE);
    final Checker c = createChecker();
    final String packageHtmlPath = getPath("package.html");
    final String filepath = getPath("InputScopeAnonInner.java");
    assertNotNull(c);
    final String[] expected = new String[]{ packageHtmlPath + ":0: missing package documentation file.", filepath + ":37:34: '(' is followed by whitespace.", filepath + ":39:42: '(' is followed by whitespace.", filepath + ":39:57: ')' is preceeded by whitespace.", filepath + ":43:14: ')' is preceeded by whitespace.", filepath + ":51:34: '(' is followed by whitespace.", filepath + ":53:42: '(' is followed by whitespace.", filepath + ":53:57: ')' is preceeded by whitespace.", filepath + ":57:14: ')' is preceeded by whitespace." };
    verify(c, filepath, expected);
}
}