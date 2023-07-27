class PlaceHold {
  public boolean hasBranch(int lineNumber) {
    return branches.containsKey(Integer.valueOf(lineNumber));
  }
}
