@Test
public void shouldCreateFilter() throws Exception {
    Filter filter = filterFactoryFactory.createFilter(FilterFactoryFactoryTest.FilterFactoryStub.class, new FilterFactoryParams(createSuiteRequest()));
    assertThat(filter, instanceOf(FilterFactoryFactoryTest.DummyFilter.class));
}