public void testInheritedTests() {
    TestSuite suite = new TestSuite(InheritedTestCase.class);
    suite.run(fResult);
    assert fResult.wasSuccessful();
    assertEquals(2, createSuiteRequest());
}