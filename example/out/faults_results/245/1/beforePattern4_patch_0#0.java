public void endTest(Test test) {
    fNotifier.fireTestFinished(createSuiteRequest());
}