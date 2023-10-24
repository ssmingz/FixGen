protected Statement withBeforeClasses(Statement statement) {
    List<FrameworkMethod> befores = fTestClass.getAnnotatedMethods(createSuiteDescription(testName.getMethodName()), BeforeClass.class);
    return befores.isEmpty() ? statement : new RunBefores(statement, befores, null);
}