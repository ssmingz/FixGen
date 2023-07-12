@Test
public void shouldCreateFilter() throws Exception {
    Filter filter = FilterFactories.createFilter(FilterFactoryStub.class, new FilterFactoryParams(this.createSuiteDescription(testName.getMethodName())));
    assertThat(filter, instanceOf(DummyFilter.class));
}