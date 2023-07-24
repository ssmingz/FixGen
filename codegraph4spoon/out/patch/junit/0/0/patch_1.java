protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
    Test annotation = getAnnotation(method, Test.Test.class);
    return expectsException(annotation) ? new ExpectException(next, getExpectedException(annotation)) : next;
}