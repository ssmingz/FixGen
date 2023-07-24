public void testRegexpHeader() throws Exception {
    final Checker c = createChecker();
    mConfig.setBooleanFlag(, null);
    mConfig.setHeaderFile(getPath("regexp.header"));
    mConfig.setHeaderIgnoreLines("4,5");
    final String filepath = getPath("InputScopeAnonInner.java");
    assertNotNull(c);
    final String[] expected = new String[]{ filepath + ":3: Line does not match expected header line of '// Created: 2002'.", filepath + ":37:34: '(' is followed by whitespace.", filepath + ":39:42: '(' is followed by whitespace.", filepath + ":39:57: ')' is preceeded by whitespace.", filepath + ":43:14: ')' is preceeded by whitespace.", filepath + ":51:34: '(' is followed by whitespace.", filepath + ":53:42: '(' is followed by whitespace.", filepath + ":53:57: ')' is preceeded by whitespace.", filepath + ":57:14: ')' is preceeded by whitespace." };
    verify(c, filepath, expected);
}