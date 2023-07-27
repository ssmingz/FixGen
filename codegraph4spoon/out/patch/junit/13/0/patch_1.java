class PlaceHold {
  @Override
  public void testRunStarted(Description description) throws Exception {
    synchronized (monitor) {
      listener.testRunStarted(description);
    }
  }
}
