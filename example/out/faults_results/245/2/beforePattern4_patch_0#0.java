public void addError(Test test, Throwable e) {
    Failure failure = new Failure(createSuiteRequest(), e);
    fNotifier.fireTestFailure(failure);
}