protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    assertTrue(clazz.getAnnotatedMethods(DataPoint.class));
}