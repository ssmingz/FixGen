@Override
protected List<FrameworkMethod> getAfters() {
    return getAnnotatedMethods(AfterClass.class);
}