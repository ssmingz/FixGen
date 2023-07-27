class PlaceHold {
  protected Object createTest() throws Exception {
    return fTestClass.getConstructor().newInstance();
  }
}
