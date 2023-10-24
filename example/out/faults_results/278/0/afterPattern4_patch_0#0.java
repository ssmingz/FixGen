public List<FrameworkMethod> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
    return Collections.unmodifiableList(createSuiteRequest());
}