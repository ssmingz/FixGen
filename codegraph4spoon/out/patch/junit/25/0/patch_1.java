class PlaceHold {
  @Override
  public void testFailure(Failure failure) throws Exception {
    synchronized (monitor) {
      listener.testFailure(failure);
    }
  }
}
