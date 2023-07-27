class PlaceHold {
  @Test
  public void packageInfoAnnotation() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(JavadocStyleCheck.class);
    final String[] expected = ArrayUtils.EMPTY_STRING_ARRAY;
    String basePath =
        (((("javadoc" + File.separator) + null) + File.separator) + "annotation") + File.separator;
    verify(createChecker(checkConfig), getPath(basePath + "package-info.java"), expected);
  }
}
