public void fireTestFailure(Failure failure) {
    fireTestFailures(listeners, asList(failure));
}