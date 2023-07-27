class PlaceHold {
  public long getTimeout() {
    Test annotation = getMethod().getAnnotation(Test.class);
    if (annotation == null) {
      return 0;
    }
    long timeout = annotation.timeout();
    return timeout;
  }
}
