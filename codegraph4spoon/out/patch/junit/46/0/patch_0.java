class PlaceHold {
  public static Request classWithoutSuiteMethod(Class<?> newTestClass) {
    return new ClassRequest(newTestClass, newSuiteBuilder());
  }
}
