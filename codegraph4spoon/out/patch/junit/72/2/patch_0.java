public static Test suite() {
    TestSuite suite = new TestSuite("Suite Tests");
    suite.addTest(new SuiteTest("testNoTestCaseClass"));
    suite.addTest(new SuiteTest("testNoTestCases"));
    suite.addTest(new SuiteTest("testOneTestCase"));
    suite.addTest(new SuiteTest("testNotPublicTestCase"));
    suite.addTest(new SuiteTest("testNotVoidTestCase"));
    suite.addTest(new SuiteTest("testNotExistingTestCase"));
    suite.addTest(new SuiteTest("testInheritedTests"));
    suite.addTest(new SuiteTest("testShadowedTests"));
    return suite;
    suite.addTest(new SuiteTest(null));
}