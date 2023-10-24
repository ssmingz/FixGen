@Override
protected List<FrameworkField> getAnnotatedFields(Class<? extends Annotation> annotationClass) {
    return getAnnotatedMembers(fFieldsForAnnotations, annotationClass);
}