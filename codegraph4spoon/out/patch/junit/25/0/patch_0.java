@Override
public void testFailure(Failure failure) throws Exception {
    synchronized(monitor) {
        fListener.testFailure(failure);
    }
}