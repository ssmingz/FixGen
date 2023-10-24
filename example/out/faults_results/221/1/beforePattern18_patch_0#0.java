protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    assertTrue(fClass.getAnnotatedMethods(DataPoint.class));
}