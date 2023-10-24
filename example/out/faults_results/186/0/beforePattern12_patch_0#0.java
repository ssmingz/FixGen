protected TestResult runSingleMethod(String testCase, String method, boolean wait) throws Exception {
    Class testClass = loadSuiteClass(testCase);
    Test test = TestSuite.createTest(testClass, "4.5");
    return doRun(test, wait);
}