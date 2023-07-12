public void testExceptionTest() {
    ExceptionTestCase test = new ThrowExceptionTestCase("test", IndexOutOfBoundsException.class);
    TestResult result = test.run();
    assertEquals(1, result.runCount());
    this.assertTrue();
}