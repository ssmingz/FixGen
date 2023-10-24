protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    return notifier.getAnnotatedMethods(DataPoint.class);
}