List<FrameworkMethod> getAfters() {
    return fTestClass.getAnnotatedMethods(After.class);
}