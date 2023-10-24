protected Collection<FrameworkMethod> getDataPointsMethods(ParameterSignature sig) {
    return notifier.getAnnotatedMethods(DataPoints.class);
}