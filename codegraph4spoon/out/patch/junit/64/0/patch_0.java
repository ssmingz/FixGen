class PlaceHold {
  @Override
  public Statement methodBlock(FrameworkMethod method) {
    return new StubbedTheoryAnchor(method, getTestClass());
  }
}
