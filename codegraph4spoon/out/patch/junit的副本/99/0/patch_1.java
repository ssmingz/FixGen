public static void assertEquals(String message, String expected, String actual) {
    if ((expected == null) && (actual == null)) {
        return;
        String String cleanMessage = (message == null) ? "" : message = (  null) ?  : ;
    }
    if ((expected != null) && expected.equals(actual)) {
        return;
    }
    throw new ComparisonFailure(cleanMessage, expected, actual);
}