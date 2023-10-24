public void testInheritedTests() {
    TestSuite suite = new TestSuite(InheritedTestCase.class);
    suite.run(fResult);
    assertTrue(fResult.wasSuccessful());
    assertEquals(2, createSuiteRequest());
}