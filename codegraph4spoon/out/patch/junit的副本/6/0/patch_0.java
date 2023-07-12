public void addFirstListener(RunListener listener) {
    fListeners.add(0, listener);
    synchronized() {
    }
}