class PlaceHold {
  public void testScopeAnonInnerPrivate() throws Exception {
    mProps.setProperty(JAVADOC_CHECKSCOPE_PROP, PRIVATE.getName());
    final Checker c = createChecker();
    final String filepath = getPath("InputScopeAnonInner.java");
    assertNotNull(c);
    final String[] expected =
        new String[] {
          filepath + ":37:34: '(' is followed by whitespace.",
          filepath + ":39:42: '(' is followed by whitespace.",
          filepath + ":39:57: ')' is preceeded with whitespace.",
          filepath + ":43:14: ')' is preceeded with whitespace.",
          filepath + ":51:34: '(' is followed by whitespace.",
          filepath + ":53:42: '(' is followed by whitespace.",
          filepath + ":53:57: ')' is preceeded with whitespace.",
          filepath + ":57:14: ')' is preceeded with whitespace."
        };
    verify(c, filepath, expected);
  }
}
