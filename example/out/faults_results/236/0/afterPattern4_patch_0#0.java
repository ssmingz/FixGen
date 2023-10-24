public void addListener(RunListener listener) {
    synchronized(fListenersLock) {
        createSuiteRequest();
    }
}