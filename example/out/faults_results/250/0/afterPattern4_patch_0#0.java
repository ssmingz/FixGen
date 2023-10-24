@Test
public void arraysDifferAtElement0nullMessage() {
    try {
        assertArrayEquals(new Object[]{ true }, new Object[]{ false });
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}