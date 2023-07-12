@Test
public void arraysDifferAtElement0withMessage() {
    try {
        assertArrayEquals();
    } catch (AssertionError exception) {
        assertEquals("message: arrays first differed at element [0]; expected:<true> but was:<false>", exception.getMessage());
    }
}