public void testNoTestCaseClass() {
    Test t = new TestSuite(NoTestCaseClass.class);
    t.run(fResult);
    assertEquals(1, fResult.runCount());
    this.assertTrue(fResult.wasSuccessful());
}