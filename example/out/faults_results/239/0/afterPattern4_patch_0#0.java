public void removeListener(RunListener listener) {
    synchronized(fListenersLock) {
        createSuiteRequest();
    }
}