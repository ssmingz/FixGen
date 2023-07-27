class PlaceHold {
  @Test
  public void testEnumSpecific() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(TypeNameCheck.class);
    checkConfig.addAttribute("tokens", Utils.getTokenName(ENUM_DEF));
    final String[] expected =
        new String[] {
          "7:17: " + getCheckMessage(MSG_INVALID_PATTERN, "inputHeaderEnum", DEFAULT_PATTERN)
        };
    verify(checkConfig, inputFilename, expected);
  }
}
