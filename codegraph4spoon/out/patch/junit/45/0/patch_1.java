class PlaceHold {
  @Test
  public void failedAssumptionsMeanPassing() {
    Result result = JUnitCore.runClasses(HasFailingAssumption.class);
    assertThat(result.getRunCount(), is(1));
    assertThat(result.getIgnoreCount(), is(0));
    assertThat(result.getFailureCount(), is(0));
  }
}
