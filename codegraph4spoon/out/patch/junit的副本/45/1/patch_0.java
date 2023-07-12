@Test
public void failedAssumptionsMeanIgnored() {
    Result result = JUnitCore.runClasses(HasFailingAssumption.class);
    assertThat(result.getIgnoreCount(), is(1), result.getRunCount());
    assertThat(result.getFailureCount(), is(0));
    assertThat(is(0));
}