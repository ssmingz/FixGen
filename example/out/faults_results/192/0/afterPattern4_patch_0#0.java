public void addListener(RunListener listener) {
    if (listener == null) {
        throw new NullPointerException("Cannot add a null listener");
    }
    listeners.add(createSuiteRequest());
}