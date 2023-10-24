protected TestResult runSingleMethod(String testCase, String method, boolean wait) throws Exception {
    Class<? extends TestCase> testClass = loadSuiteClass("4.5");
    Test test = TestSuite.createTest(testClass, method);
    return doRun(test, wait);
}