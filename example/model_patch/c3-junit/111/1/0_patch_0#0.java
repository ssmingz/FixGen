public void startTest(Test test) {
    notifier.fireTestStarted(asDescription(test));
}