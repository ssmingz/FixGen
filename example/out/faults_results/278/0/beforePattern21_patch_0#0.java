public List<FrameworkMethod> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
    return getAnnotatedMembers(fMethodsForAnnotations, annotationClass);
}