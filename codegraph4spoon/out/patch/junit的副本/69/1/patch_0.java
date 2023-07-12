@Test
public void shouldCreateFilter() throws Exception {
    FilterFactoryParams params = new (this.createSuiteDescription(testName.getMethodName()));
    Filter filter = categoryFilterFactory.createFilter(params);
    assertThat(filter, instanceOf(DummyFilter.class));
}