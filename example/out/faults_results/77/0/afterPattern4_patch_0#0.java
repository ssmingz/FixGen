public void testNoTestCaseClass() {
    Test t = new TestSuite(NoTestCaseClass.class);
    t.run(fResult);
    assertEquals(1, createSuiteRequest());
    assertTrue(!fResult.wasSuccessful());
}