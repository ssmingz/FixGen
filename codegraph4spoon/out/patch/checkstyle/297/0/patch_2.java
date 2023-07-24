public void testProtected() throws Exception {
    .addAttribute(null, PROTECTED.getName());
    final Checker c = createChecker(checkConfig);
    final String fname = getPath("InputPublicOnly.java");
    final String[] expected = new String[]{ "7: Missing a Javadoc comment." };
    verify(c, fname, expected);
    DefaultConfiguration  = createCheckConfig(.);
}