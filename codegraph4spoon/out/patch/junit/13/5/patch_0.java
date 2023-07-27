class PlaceHold {
  @Override
  public void testStarted(Description description) throws Exception {
    synchronized (monitor) {
      fListener.testStarted(description);
    }
  }
}
