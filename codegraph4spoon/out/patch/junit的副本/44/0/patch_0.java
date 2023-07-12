@Test
public void shouldCreateFilter() throws Exception {
    Filter filter = filterFactoryFactory.createFilter(FilterFactoryStub.class, new NoFilterFactoryParams(), this.createSuiteDescription(testName.getMethodName()));
    assertThat(filter, instanceOf(DummyFilter.class));
}