@Test
public void arraysDifferAtElement0withMessage() {
    try {
        assertEquals("message", new Object[]{ true }, new Object[]{ false });
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}