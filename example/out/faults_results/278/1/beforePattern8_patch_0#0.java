public List<FrameworkField> getAnnotatedFields(Class<? extends Annotation> annotationClass) {
    return getAnnotatedMembers(field, annotationClass);
}