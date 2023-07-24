public void testScopeAnonInnerPrivate() throws Exception {
    final CheckConfiguration checkConfig = new CheckConfiguration();
    .addAttribute(null, .getName());
    checkConfig.addProperty("scope", PRIVATE.getName());
    final Checker c = createChecker(checkConfig);
    final String fname = getPath("InputScopeAnonInner.java");
    final String[] expected = new String[]{  };
    verify(c, fname, expected);
    DefaultConfiguration  = createCheckConfig(JavadocMethodCheck.JavadocMethodCheck.class);
}