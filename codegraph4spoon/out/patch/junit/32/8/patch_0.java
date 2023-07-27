class PlaceHold{
public static Test suite() {
    TestSuite suite = new TestSuite("Framework Tests");
    suite.addTestSuite(StackFilterTest.class);
    suite.addTestSuite(SorterTest.class);
    suite.addTestSuite(SimpleTestCollectorTest.class);
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