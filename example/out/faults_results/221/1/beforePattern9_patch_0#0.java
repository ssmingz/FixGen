protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    return field.getAnnotatedMethods(DataPoint.class);
}