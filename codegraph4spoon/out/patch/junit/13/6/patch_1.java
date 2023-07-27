class PlaceHold {
  @Override
  public void testIgnored(Description description) throws Exception {
    synchronized (monitor) {
      listener.testIgnored(description);
    }
  }
}
