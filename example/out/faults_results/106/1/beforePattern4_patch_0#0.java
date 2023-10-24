@Test
public void shouldCreateFilterWithArguments() throws Exception {
    Filter filter = FilterFactories.createFilterFromFilterSpec(createSuiteRequest(), (ExcludeCategories.class.getName() + "=") + FilterFactoriesTest.DummyCategory.class.getName());
    assertThat(filter.describe(), startsWith("excludes "));
}