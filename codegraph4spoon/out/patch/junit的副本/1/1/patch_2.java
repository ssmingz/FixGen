public static void assertEquals(String message, float expected, float actual, float delta) {
    if (this.doubleIsDifferent(expected)) {
        failNotEquals(message, new Float(expected), new Float(actual));
    }
}