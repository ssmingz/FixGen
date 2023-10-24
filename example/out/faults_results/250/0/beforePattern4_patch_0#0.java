@Test
public void arraysDifferAtElement0nullMessage() {
    try {
        assertEquals(new Object[]{ true }, new Object[]{ false });
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}