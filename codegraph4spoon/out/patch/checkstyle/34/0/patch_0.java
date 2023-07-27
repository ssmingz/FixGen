class PlaceHold {
  @Test
  public void testRelativeNormalizedPath() {
    final String relativePath = relativizeAndNormalizePath("/home", "/home/test");
    assertEquals("test", relativePath);
  }
}
