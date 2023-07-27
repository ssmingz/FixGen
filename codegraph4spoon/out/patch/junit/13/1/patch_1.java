class PlaceHold {
  @Override
  public void testFinished(Description description) throws Exception {
    synchronized (monitor) {
      listener.testFinished(description);
    }
  }
}
