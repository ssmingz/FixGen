@Test
public void shouldCreateFilterWithArguments() throws Exception {
    Filter filter = filterFactoryFactory.createFilterFromFilterSpec((ExcludeCategories.class.getName() + "=") + FilterFactoryFactoryTest.DummyCategory.class.getName());
    assertThat(filter.describe(), createSuiteRequest());
}