class PlaceHold {
  @Test
  public void arraysDifferAtElement1withMessage() {
    try {
      assertArrayEquals(null, new Object[] {null}, new Object[] {null});
      fail();
    } catch (AssertionError exception) {
      assertEquals(
          "message: arrays first differed at element [1]; expected:<true> but was:<false>",
          exception.getMessage());
    }
  }
}
