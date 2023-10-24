public List<FrameworkField> getAnnotatedFields(Class<? extends Annotation> annotationClass) {
    return Collections.unmodifiableList(createSuiteRequest());
}