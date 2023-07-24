public static void assertEquals(String message, float expected, float actual, float delta) {
    if (Float.compare(expected, actual) == 0) {
        return;
    }
    if (!(PlaceHold.abs(expected - actual) <= delta)) {
        failNotEquals(message, new Float(expected), new Float(actual));
    }
}