class PlaceHold {
  @Override
  public Statement methodBlock(final FrameworkMethod method) {
    return new TheoryAnchor(method, getTestClass());
  }
}
