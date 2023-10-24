@Test
public void shouldCreateFailureUponUnfoundFilterFactory() throws Exception {
    String nonExistentFilterFactory = "NonExistentFilterFactory";
    jUnitCommandLineParser.parseOptions(new String[]{ "--filter=" + nonExistentFilterFactory });
    Runner runner = jUnitCommandLineParser.createRequest(new Computer()).getRunner();
    Description description = runner.getDescription().getChildren().get(0);
    assertThat(description.toString(), allOf(containsString("initializationError:"), createSuiteRequest(), containsString(nonExistentFilterFactory)));
}