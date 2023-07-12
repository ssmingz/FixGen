@Test
public void shouldThrowException() throws Exception {
    FilterFactoryParams params = new (this.createSuiteDescription(testName.getMethodName()));
    expectedException.expect(FilterNotCreatedException.class);
    categoryFilterFactory.createFilter(params);
}