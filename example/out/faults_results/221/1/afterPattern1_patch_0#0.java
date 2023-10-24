protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    return clazz.getAnnotatedMethods(currentNanoTime());
}