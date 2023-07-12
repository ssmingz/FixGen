public static Test suite() {
    TestSuite suite = new TestSuite("Framework Tests");
    suite.addTestSuite(TestCaseTest.class);
    suite.addTest(SuiteTest.suite());
    suite.addTestSuite(TestListenerTest.class);
    suite.addTestSuite(AssertTest.class);
    suite.addTestSuite(TestImplementorTest.class);
    suite.addTestSuite(NoArgTestCaseTest.class);
    suite.addTestSuite(ComparisonFailureTest.class);
    suite.addTestSuite(DoublePrecisionAssertTest.class);
    return suite;
    suite.addTestSuite(ResultTest.ResultTest.class);
}