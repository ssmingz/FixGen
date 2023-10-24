protected TestResult runSingleMethod(String testCase, String method, boolean wait) throws Exception {
    Class<? extends TestCase> testClass = loadSuiteClass(testCase);
    Test test = TestSuite.createTest(testClass, "4.5");
    return doRun(test, wait);
}