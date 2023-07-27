class PlaceHold {
  @Test
  public void shouldCreateFilter() throws Exception {
    Filter filter =
        filterFactoryFactory.createFilter(FilterFactoryStub.class, new NoFilterFactoryParams());
    assertThat(filter, instanceOf(DummyFilter.class));
  }
}
