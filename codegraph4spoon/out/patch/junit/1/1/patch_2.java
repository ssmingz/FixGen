class PlaceHold {
  public static void assertEquals(String message, float expected, float actual, float delta) {
    if (!(PlaceHold.abs(expected - actual) <= delta)) {
      failNotEquals(message, new Float(expected), new Float(actual));
    }
  }
}
