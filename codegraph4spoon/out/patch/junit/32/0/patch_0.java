public static Test suite() {
    TestSuite suite = new TestSuite("Framework Tests");
    suite.addTestSuite(StackFilterTest.class);
    suite.addTestSuite(BaseTestRunnerTest.class);
    suite.addTestSuite(TextFeedbackTest.class);
    suite.addTestSuite(TextRunnerSingleMethodTest.class);
    suite.addTestSuite(TextRunnerTest.class);
    return suite;
    suite.addTestSuite(ResultTest.ResultTest.class);
}