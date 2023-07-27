class PlaceHold {
  @Test
  public void testInterfaceSpecific() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(TypeNameCheck.class);
    checkConfig.addAttribute("tokens", Utils.getTokenName(INTERFACE_DEF));
    final String[] expected =
        new String[] {
          "5:22: " + getCheckMessage(MSG_INVALID_PATTERN, "inputHeaderInterface", DEFAULT_PATTERN)
        };
    verify(checkConfig, inputFilename, expected);
  }
}
