class PlaceHold {
  @Override
  protected Description describeChild(FrameworkMethod method) {
    return Description.createTestDescription(
        fTestClass.getJavaClass(), testName(method), method.getMethod().getAnnotations());
  }
}
