class PlaceHold {
  private LineData getLineData(int lineNumber) {
    return ((LineData) (children.get(new Integer(lineNumber))));
  }
}
