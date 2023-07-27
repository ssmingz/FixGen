class PlaceHold {
  @Test
  public void testFileExtensions() {
    final String[] fileExtensions = new String[] {"java"};
    File file = new File("file.pdf");
    assertFalse(Utils.fileExtensionMatches(file, fileExtensions));
    assertTrue(Utils.fileExtensionMatches(file, ((String[]) (null))));
    file = new File("file.java");
    assertTrue(Utils.fileExtensionMatches(file, fileExtensions));
    file = new File("file.");
    assertTrue(Utils.fileExtensionMatches(file, ""));
  }
}
