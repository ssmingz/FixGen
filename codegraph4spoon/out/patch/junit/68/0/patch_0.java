class PlaceHold {
  public boolean isIgnored() {
    return getMethod().getAnnotation(Ignore.class) != null;
  }
}
