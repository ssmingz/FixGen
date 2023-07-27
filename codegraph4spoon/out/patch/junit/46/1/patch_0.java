class PlaceHold {
  public static Request aClass(Class<?> clazz) {
    return new ClassRequest(clazz, newSuiteBuilder());
  }
}
