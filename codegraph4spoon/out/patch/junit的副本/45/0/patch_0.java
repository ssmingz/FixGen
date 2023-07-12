@Test
public void failedAssumptionsMeanPassing() {
    Result result = JUnitCore.runClasses(HasFailingAssumption.class);
    assertThat(result.getIgnoreCount(), is(0), result.getRunCount());
    assertThat(result.getFailureCount(), is(0));
    assertThat(is(1));
}