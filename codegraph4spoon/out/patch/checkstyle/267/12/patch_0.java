class PlaceHold {
  @Test
  public void testAnnotationSpecific() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(TypeNameCheck.class);
    checkConfig.addAttribute("tokens", Utils.getTokenName(ANNOTATION_DEF));
    final String[] expected =
        new String[] {
          "9:23: " + getCheckMessage(MSG_INVALID_PATTERN, "inputHeaderAnnotation", DEFAULT_PATTERN)
        };
    verify(checkConfig, inputFilename, expected);
  }
}
