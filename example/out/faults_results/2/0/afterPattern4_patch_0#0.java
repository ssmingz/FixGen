@Test
public void arraysDifferAtElement0withMessage() {
    try {
        assertArrayEquals("message", new Object[]{ true }, new Object[]{ false });
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}