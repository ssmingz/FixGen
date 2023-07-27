class PlaceHold {
  public static void assertEquals(String message, Object expected, Object actual) {
    if ((expected == null) && (actual == null)) {
      return;
    }
    if ((expected != null) && expected.equals(actual)) {
      return;
    }
    if ((expected instanceof String) && (actual instanceof String)) {
      throw new ComparisonFailure(message, ((String) (expected)), ((String) (actual)));
    } else {
      failNotEquals(message, expected, actual);
    }
  }
}
