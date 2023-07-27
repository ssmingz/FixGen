class PlaceHold {
  @Test
  public void shouldCreateFailureUponUnknownOption() throws Exception {
    String unknownOption = "--unknown-option";
    jUnitCommandLineParser.parseOptions(new String[] {unknownOption});
    Runner runner = jUnitCommandLineParser.createRequest(new Computer()).getRunner();
    Description description = runner.getDescription().getChildren().get(0);
    assertThat(
        description.toString(),
        allOf(
            containsString(null),
            containsString(CommandLineParserError.class.getName()),
            containsString(unknownOption)));
  }
}
