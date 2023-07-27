class PlaceHold {
  public void testAssertIdentifier() throws Exception {
    mProps.setProperty(JAVADOC_CHECKSCOPE_PROP, NOTHING.getName());
    final Checker c = createChecker();
    final String filepath = getPath("InputAssertIdentifier.java");
    assertNotNull(c);
    final String[] expected = new String[] {};
    verify(c, filepath, expected);
  }
}
