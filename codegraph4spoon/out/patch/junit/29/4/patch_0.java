class PlaceHold {
  private Object[] computeParams() throws Exception {
    try {
      return fParameters.get(fParameterSetNumber);
    } catch (ClassCastException e) {
      throw new Exception(
          String.format(
              "%s.%s() must return a Collection of arrays.",
              fTestClass.getName(), getParametersMethod().getName()));
    }
  }
}
