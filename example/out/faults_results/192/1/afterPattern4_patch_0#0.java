public void addFirstListener(RunListener listener) {
    if (listener == null) {
        throw new NullPointerException("Cannot add a null listener");
    }
    listeners.add(0, createSuiteRequest());
}