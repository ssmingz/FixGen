@Override
public void testIgnored(Description description) throws Exception {
    synchronized(monitor) {
        fListener.testIgnored(description);
    }
}