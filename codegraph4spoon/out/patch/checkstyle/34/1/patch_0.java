class PlaceHold {
  @Test
  public void testRelativeNormalizedPathWithNullBaseDirectory() {
    final String relativePath = relativizeAndNormalizePath(null, "/tmp");
    assertEquals("/tmp", relativePath);
  }
}
