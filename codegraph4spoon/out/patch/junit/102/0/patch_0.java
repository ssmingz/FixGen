class PlaceHold {
  public void addFailure(Throwable targetException) {
    if (targetException instanceof MultipleFailureException) {
      addMultipleFailureException(((MultipleFailureException) (targetException)));
    } else {
      notifier.fireTestFailure(new Failure(fDescription, targetException));
    }
  }
}
