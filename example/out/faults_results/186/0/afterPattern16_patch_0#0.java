protected TestResult runSingleMethod(String testCase, String method, boolean wait) throws Exception {
    Class<? extends TestCase> testClass = loadSuiteClass(testCase);
    Test test = TestSuite.createTest(createSuiteDescription(testName.getMethodName()), testClass, method);
    return doRun(test, wait);
}