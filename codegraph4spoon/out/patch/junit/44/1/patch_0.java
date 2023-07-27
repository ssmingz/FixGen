class PlaceHold {
  @Test
  public void shouldCreateFilterWithNoArguments() throws Exception {
    Filter filter =
        filterFactoryFactory.createFilterFromFilterSpec(FilterFactoryStub.class.getName());
    assertThat(filter, instanceOf(DummyFilter.class));
  }
}
