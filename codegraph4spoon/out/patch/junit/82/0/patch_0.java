class PlaceHold {
  public void testAssertEqualsNaNFails() {
    try {
      assertEquals(1.234, Double.NaN, null);
    } catch (AssertionFailedError e) {
      return;
    }
    fail();
  }
}
