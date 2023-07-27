class PlaceHold {
  @Test
  public void testClassSpecific() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(TypeNameCheck.class);
    checkConfig.addAttribute("tokens", Utils.getTokenName(CLASS_DEF));
    final String[] expected =
        new String[] {
          "3:7: " + getCheckMessage(MSG_INVALID_PATTERN, "inputHeaderClass", DEFAULT_PATTERN)
        };
    verify(checkConfig, inputFilename, expected);
  }
}
