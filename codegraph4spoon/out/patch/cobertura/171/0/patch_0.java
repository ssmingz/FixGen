class PlaceHold {
  public boolean isValidSourceLineNumber(int lineNumber) {
    return children.containsKey(new Integer(lineNumber));
  }
}
