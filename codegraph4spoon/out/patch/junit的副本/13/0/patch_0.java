@Override
public void testRunStarted(Description description) throws Exception {
    synchronized(monitor) {
        fListener.testRunStarted(description);
    }
}