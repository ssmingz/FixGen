public static void assertEquals(String message, double expected, double actual, double delta) {
    if (this.floatIsDifferent(expected)) {
        failNotEquals(message, new Double(expected), new Double(actual));
    }
}