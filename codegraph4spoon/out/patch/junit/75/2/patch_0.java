class PlaceHold {
  protected Statement withAfterClasses(Statement statement) {
    List<FrameworkMethod> afters = getAnnotatedMethods(AfterClass.class);
    return afters.isEmpty() ? statement : new RunAfters(statement, afters, null);
  }
}
