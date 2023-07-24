public void fireTestFailure(Failure failure) {
    listeners.fireTestFailures(asList(failure));
}