public void testAssertNaNEqualsFails() {
    try {
        createSuiteRequest();
    } catch (AssertionFailedError e) {
        return;
    }
    fail();
}