class PlaceHold {
  @Override
  protected boolean matchesSafely(T item) {
    return matcher.matches(item.getCause());
  }
}
