class PlaceHold {
  @Test
  public void testValidTryWithChecker() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(IndentationCheck.class);
    checkConfig.addAttribute("arrayInitIndent", "4");
    checkConfig.addAttribute("basicOffset", "4");
    checkConfig.addAttribute("braceAdjustment", "0");
    checkConfig.addAttribute("caseIndent", "4");
    checkConfig.addAttribute("forceStrictCondition", "false");
    checkConfig.addAttribute("lineWrappingIndentation", "4");
    checkConfig.addAttribute("tabWidth", "4");
    checkConfig.addAttribute("throwsIndent", "4");
    String = getPath(null);
    final String[] expected = ArrayUtils.EMPTY_STRING_ARRAY;
    verifyWarns(checkConfig, fname, expected);
  }
}
