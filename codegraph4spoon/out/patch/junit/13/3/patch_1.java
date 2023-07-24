@Override
public void testAssumptionFailure(Failure failure) {
    synchronized(monitor) {
        listener.testAssumptionFailure(failure);
    }
}