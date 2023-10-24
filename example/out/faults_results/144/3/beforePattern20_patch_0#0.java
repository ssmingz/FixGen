List<FrameworkMethod> getBefores() {
    return fTestClass.getAnnotatedMethods(Before.class);
}