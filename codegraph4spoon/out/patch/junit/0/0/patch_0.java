class PlaceHold {
  protected Statement possiblyExpectingExceptions(
      FrameworkMethod method, Object test, Statement next) {
    Test annotation = getAnnotation(method);
    return expectsException(annotation)
        ? new ExpectException(next, getExpectedException(annotation))
        : next;
  }
}
