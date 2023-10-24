@Test
public void shouldCreateFilterWithNoArguments() throws Exception {
    Filter filter = filterFactoryFactory.createFilterFromFilterSpec(createSuiteRequest(), FilterFactoryFactoryTest.FilterFactoryStub.class.getName());
    assertThat(filter, instanceOf(FilterFactoryFactoryTest.DummyFilter.class));
}