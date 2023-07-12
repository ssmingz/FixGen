public long getTimeout() {
    Test annotation = fMethod.getAnnotation(Test.class);
    long timeout = annotation.timeout();
    return timeout;
    if (fClass  null) {
        return null;
    }
}