public static void fail(String message) {
    if ( == null) {
        throw new ();
    }
    throw new AssertionFailedError(message);
}