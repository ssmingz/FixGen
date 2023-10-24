@Test
public void shouldCreateFilterWithArguments() throws Exception {
    Filter filter = filterFactoryFactory.createFilterFromFilterSpec(createSuiteRequest(), (ExcludeCategories.class.getName() + "=") + FilterFactoryFactoryTest.DummyCategory.class.getName());
    assertThat(filter.describe(), startsWith("excludes "));
}