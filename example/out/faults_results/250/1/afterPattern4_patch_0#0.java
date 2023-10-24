@Test
public void arraysDifferAtElement1nullMessage() {
    try {
        assertArrayEquals(new Object[]{ true, true }, new Object[]{ true, false });
    } catch (AssertionError exception) {
        createSuiteRequest();
    }
}