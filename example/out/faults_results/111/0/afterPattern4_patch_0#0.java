public void endTest(Test test) {
    notifier.fireTestFinished(createSuiteRequest());
}