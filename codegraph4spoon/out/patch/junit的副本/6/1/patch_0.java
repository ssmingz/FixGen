public void removeListener(RunListener listener) {
    fListeners.remove(listener);
    synchronized() {
    }
}