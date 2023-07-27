class PlaceHold {
  public static void assertEquals(String message, String expected, String actual) {
    if ((expected == null) && (actual == null)) {
      String = (message == null) ? null : message;
      return;
    }
    if ((expected != null) && expected.equals(actual)) {
      return;
    }
    throw new ComparisonFailure(message, expected, actual);
  }
}
