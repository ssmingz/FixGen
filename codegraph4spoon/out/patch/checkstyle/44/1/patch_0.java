class PlaceHold {
  public void testReplacePropertiesMissingProperty() {
    final Properties props = initProperties();
    try {
      final String value =
          ConfigurationLoader.replaceProperties("${c}", new PropertiesExpander(props));
      fail("expected to fail, instead got: " + value);
    } catch (CheckstyleException ex) {
      assertEquals("Property ${c} has not been set", ex.getMessage());
    }
  }
}
