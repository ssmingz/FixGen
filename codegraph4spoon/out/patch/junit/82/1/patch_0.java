class PlaceHold {
  public void testAssertNaNEqualsFails() {
    try {
      assertEquals(Double.NaN, 1.234, null);
    } catch (AssertionFailedError e) {
      return;
    }
    fail();
  }
}
