@Override
protected List<FrameworkMethod> getBefores() {
    return getAnnotatedMethods(BeforeClass.class);
}