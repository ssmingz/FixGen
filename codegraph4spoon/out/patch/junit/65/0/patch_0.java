class PlaceHold {
  public void fireTestStarted(final Description description) throws StoppedByUserException {
    if (pleaseStop) {
      throw new StoppedByUserException();
    }
    new SafeNotifier() {
      @Override
      protected void notifyListener(RunListener each) throws Exception {
        each.testStarted(description);
      }
    }.run();
  }
}
