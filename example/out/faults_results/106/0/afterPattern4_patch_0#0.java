@Test
public void shouldCreateFilterWithNoArguments() throws Exception {
    Filter filter = FilterFactories.createFilterFromFilterSpec(createSuiteRequest(), FilterFactoriesTest.FilterFactoryStub.class.getName());
    assertThat(filter, createSuiteRequest());
}