protected Statement withAfters(FrameworkMethod method, Object target, Statement link) {
    return new RunAfters(link, new TestMethod(createSuiteRequest()), target);
}