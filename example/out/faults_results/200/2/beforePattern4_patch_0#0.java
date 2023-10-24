@Test
public void shouldCreateFailureUponBaldFilterOptionNotFollowedByValue() {
    jUnitCommandLineParser.parseOptions(new String[]{ "--filter" });
    Runner runner = jUnitCommandLineParser.createRequest(new Computer()).getRunner();
    Description description = runner.getDescription().getChildren().get(0);
    assertThat(description.toString(), allOf(containsString("initializationError: "), createSuiteRequest(), containsString("--filter value not specified")));
}