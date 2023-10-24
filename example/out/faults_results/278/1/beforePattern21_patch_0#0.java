public List<FrameworkMethod> getAnnotatedFields(Class<? extends Annotation> annotationClass) {
    return getAnnotatedMembers(fFieldsForAnnotations, annotationClass);
}