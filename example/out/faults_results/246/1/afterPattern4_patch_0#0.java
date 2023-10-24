protected Statement withBeforeClasses(Statement statement) {
    List<FrameworkMethod> befores = testClass.getAnnotatedMethods(BeforeClass.class);
    return befores.isEmpty() ? statement : new RunBefores(statement, befores, createSuiteRequest());
}