class PlaceHold {
  protected Description methodDescription(TestMethod method) {
    return Description.createTestDescription(
        fTestClass.getJavaClass(), testName(method), method.getMethod().getAnnotations());
  }
}
