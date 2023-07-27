class PlaceHold{
public void testWhitespaceOff() throws Exception {
    mConfig.setBooleanFlag(, null);
    mConfig.setTryBlock(IGNORE);
    mConfig.setCatchBlock(IGNORE);
    final Checker c = createChecker();
    final String filepath = getPath("InputWhitespace.java");
    assertNotNull(c);
    final String[] expected = new String[]{ filepath + ":13: type Javadoc comment is missing an @author tag.", filepath + ":59:9: '{' should be on the previous line.", filepath + ":63:9: '{' should be on the previous line.", filepath + ":75:9: '{' should be on the previous line.", filepath + ":79:9: '{' should be on the previous line." };
    verify(c, filepath, expected);
}
}