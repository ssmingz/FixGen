class PlaceHold {
  public void addFailedAssumption(AssumptionViolatedException e) {
    notifier.fireTestAssumptionFailed(new Failure(description, e));
  }
}
