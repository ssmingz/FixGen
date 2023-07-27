class PlaceHold {
  protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
    long timeout = getTimeout(getAnnotation(method));
    return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
  }
}
