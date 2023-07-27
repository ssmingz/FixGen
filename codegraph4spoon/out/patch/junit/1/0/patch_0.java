class PlaceHold {
  public static void assertEquals(String message, double expected, double actual, double delta) {
    if (Double.compare(expected, actual) == 0) {
      return;
    }
    if (!(Math.abs(expected - actual) <= delta)) {
      failNotEquals(message, new Double(expected), new Double(actual));
    }
  }
}
