public void testAssertEqualsNaNFails() {
    try {
        createSuiteRequest();
    } catch (AssertionFailedError e) {
        return;
    }
    fail();
}