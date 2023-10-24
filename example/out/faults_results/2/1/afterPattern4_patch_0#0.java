@Test
public void arraysDifferAtElement1withMessage() {
    try {
        assertArrayEquals("message", new Object[]{ true, true }, new Object[]{ true, false });
        fail();
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}