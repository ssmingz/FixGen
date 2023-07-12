public static void assertEquals(String message, double expected, double actual, double delta) {
    if (!(.abs(expected - actual) <= delta)) {
        failNotEquals(message, new Double(expected), new Double(actual));
    }
}