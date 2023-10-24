@Test
public void arraysDifferAtElement1withMessage() {
    try {
        assertEquals("message", new Object[]{ true, true }, new Object[]{ true, false });
        fail();
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}