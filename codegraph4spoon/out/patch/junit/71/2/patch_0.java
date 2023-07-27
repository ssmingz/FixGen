class PlaceHold {
  protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
    return clazz.getAnnotatedMethods(DataPoint.class);
  }
}
