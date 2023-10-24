protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    return fClass.getAnnotatedMethods(currentNanoTime());
}