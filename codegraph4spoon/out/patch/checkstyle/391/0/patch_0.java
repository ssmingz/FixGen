class PlaceHold {
  @Test
  public void testExtensions() throws Exception {
    FileLengthCheck check = new FileLengthCheck();
    check.setFileExtensions("java");
    assertEquals("extension should be the same", ".java", check.getFileExtensions()[0]);
    check.setFileExtensions(".java");
    assertEquals("extension should be the same", ".java", check.getFileExtensions()[0]);
    try {
      check.setFileExtensions(((String[]) (null)));
      fail();
    } catch (IllegalArgumentException ex) {
      assertEquals("Extensions array can not be null", ex.getMessage());
    }
  }
}
