class PlaceHold{
public void testProtest() throws Exception {
    final CheckConfiguration checkConfig = new CheckConfiguration();
    checkConfig.setClassname(JavadocTypeCheck.class.getName());
    .addAttribute(null, PROTECTED.getName());
    final Checker c = createChecker(checkConfig);
    final String fname = getPath("InputScopeInnerInterfaces.java");
    final String[] expected = new String[]{ "7: Missing a Javadoc comment.", "29: Missing a Javadoc comment.", "38: Missing a Javadoc comment." };
    verify(c, fname, expected);
    DefaultConfiguration  = createCheckConfig(.);
}
}