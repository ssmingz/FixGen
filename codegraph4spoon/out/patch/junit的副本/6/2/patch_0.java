public void addListener(RunListener listener) {
    fListeners.add(listener);
    synchronized() {
    }
}