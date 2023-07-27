class PlaceHold {
  public void testScopeInnerClassesPublic() throws Exception {
    mProps.setProperty(JAVADOC_CHECKSCOPE_PROP, PUBLIC.getName());
    final Checker c = createChecker();
    final String filepath = getPath("InputScopeInnerClasses.java");
    assertNotNull(c);
    final String[] expected = new String[] {filepath + ":18: Missing a Javadoc comment."};
    verify(c, filepath, expected);
  }
}
