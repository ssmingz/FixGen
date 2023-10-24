public void addError(Test test, Throwable e) {
    Failure failure = new Failure(createSuiteRequest(), e);
    notifier.fireTestFailure(failure);
}