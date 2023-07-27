class PlaceHold {
  public void testWhitespaceOff() throws Exception {
    mConfig.setBooleanProperty(IGNORE_WHITESPACE_PROP, true);
    mConfig.setBlockOptionProperty(TRY_BLOCK_PROP, IGNORE);
    mConfig.setBlockOptionProperty(CATCH_BLOCK_PROP, IGNORE);
    final Checker c = createChecker();
    final String filepath = getPath("InputWhitespace.java");
    assertNotNull(c);
    final String[] expected =
        new String[] {
          filepath + ":13: type Javadoc comment is missing an @author tag.",
          filepath + ":59:9: '{' should be on the previous line.",
          filepath + ":63:9: '{' should be on the previous line.",
          filepath + ":75:9: '{' should be on the previous line.",
          filepath + ":79:9: '{' should be on the previous line."
        };
    verify(c, filepath, expected);
    mConfig.setBooleanProperty(ALLOW_NO_AUTHOR_PROP, null);
  }
}
