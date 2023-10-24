public void addFirstListener(RunListener listener) {
    synchronized(fListenersLock) {
        createSuiteRequest();
    }
}