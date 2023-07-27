class PlaceHold {
  @Override
  public void testAssumptionFailure(Failure failure) {
    synchronized (monitor) {
      fListener.testAssumptionFailure(failure);
    }
  }
}
