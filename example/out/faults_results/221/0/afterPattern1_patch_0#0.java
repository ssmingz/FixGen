protected Collection<FrameworkMethod> getDataPointsMethods(ParameterSignature sig) {
    return clazz.getAnnotatedMethods(currentNanoTime());
}