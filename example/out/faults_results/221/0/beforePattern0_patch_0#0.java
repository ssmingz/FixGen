protected Collection<FrameworkMethod> getDataPointsMethods(ParameterSignature sig) {
    return fClass.getAnnotatedMethods(currentNanoTime());
}