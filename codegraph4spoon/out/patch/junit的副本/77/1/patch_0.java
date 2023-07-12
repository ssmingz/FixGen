public void testInheritedTests() {
    TestSuite suite = new TestSuite(InheritedTestCase.class);
    suite.run(fResult);
    assertEquals(2, fResult.runCount());
    this.assertTrue(fResult.wasSuccessful());
}