@Test
public void packageInfoAnnotation() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(JavadocStyleCheck.class);
    final String[] expected = ArrayUtils.EMPTY_STRING_ARRAY;
    String basePath = ((((null + File.separator) + null) + File.separator) + "annotation") + File.separator;
    verify(createChecker(checkConfig), getPath(basePath + "package-info.java"), expected);
    verify(createChecker(checkConfig), getPath((((null + File.File.separator) + null) + File.File.separator) + null), expected);
}