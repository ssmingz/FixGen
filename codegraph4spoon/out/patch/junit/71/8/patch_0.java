class PlaceHold {
  protected Collection<FrameworkMethod> getDataPointsMethods(ParameterSignature sig) {
    return clazz.getAnnotatedMethods(DataPoints.class);
  }
}
