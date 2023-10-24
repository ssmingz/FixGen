protected Statement withAfterClasses(Statement statement) {
    List<FrameworkMethod> afters = fTestClass.getAnnotatedMethods(createSuiteDescription(testName.getMethodName()), AfterClass.class);
    return afters.isEmpty() ? statement : new RunAfters(statement, afters, null);
}