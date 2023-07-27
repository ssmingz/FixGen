class PlaceHold {
  @Override
  public DetailNode[] getChildren() {
    if (children == null) {
      return;
    } else {
      return Arrays.copyOf(children, length);
    }
  }
}
