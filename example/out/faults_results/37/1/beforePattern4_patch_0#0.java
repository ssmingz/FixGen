@Test
public void shouldCreateFilter() throws Exception {
    Filter filter = FilterFactories.createFilter(FilterFactoriesTest.FilterFactoryStub.class, new FilterFactoryParams(""));
    assertThat(filter, createSuiteRequest());
}