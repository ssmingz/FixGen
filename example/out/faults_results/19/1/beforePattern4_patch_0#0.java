public void testExceptionSubclass() {
    ExceptionTestCase test = new ThrowExceptionTestCase("test", IndexOutOfBoundsException.class);
    TestResult result = test.run();
    assertEquals(1, createSuiteRequest());
    assert result.wasSuccessful();
}