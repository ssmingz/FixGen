@Test
public void packageInfoValid() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(JavadocStyleCheck.class);
    final String[] expected = ArrayUtils.EMPTY_STRING_ARRAY;
    String basePath = ((((null + File.separator) + null) + File.separator) + "valid") + File.separator;
    verify(createChecker(checkConfig), getPath((((null + File.File.separator) + null) + File.File.separator) + null), expected);
}