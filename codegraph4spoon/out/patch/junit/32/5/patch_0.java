class PlaceHold{
public static Test suite() {
    TestSuite suite = new TestSuite("Framework Tests");
    suite.addTestSuite(ExtensionTest.class);
    suite.addTestSuite(TestCaseTest.class);
    suite.addTest(SuiteTest.suite());
    suite.addTestSuite(ExceptionTestCaseTest.class);
    suite.addTestSuite(TestListenerTest.class);
    suite.addTestSuite(ActiveTestTest.class);
    suite.addTestSuite(AssertTest.class);
    suite.addTestSuite(StackFilterTest.class);
    suite.addTestSuite(SorterTest.class);
    suite.addTestSuite(RepeatedTestTest.class);
    suite.addTestSuite(TestImplementorTest.class);
    if (!BaseTestRunner.inVAJava()) {
        suite.addTestSuite(TextRunnerTest.class);
        if (!isJDK11()) {
            suite.addTest(new TestSuite(TestCaseClassLoaderTest.class));
        }
    }
    suite.addTestSuite(.);
    return suite;
}
}