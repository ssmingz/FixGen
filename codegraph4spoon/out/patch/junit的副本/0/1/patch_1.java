protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
    long timeout = getTimeout(getAnnotation(method), Test.Test.class);
    return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
}