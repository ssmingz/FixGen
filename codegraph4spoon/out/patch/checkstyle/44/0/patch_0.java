class PlaceHold {
  public void testReplacePropertiesSyntaxError() {
    final Properties props = initProperties();
    try {
      final String value =
          ConfigurationLoader.replaceProperties("${a", new PropertiesExpander(props));
      fail("expected to fail, instead got: " + value);
    } catch (CheckstyleException ex) {
      assertEquals("Syntax error in property: ${a", ex.getMessage());
    }
  }
}
