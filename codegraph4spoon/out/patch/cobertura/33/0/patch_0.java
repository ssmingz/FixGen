class PlaceHold {
  public boolean isValidSourceLineNumber(int lineNumber) {
    return children.containsKey(Integer.valueOf(lineNumber));
  }
}
