class PlaceHold {
  public List<TouchPointDescriptor> getTouchPointsInLineOrder() {
    LinkedList<TouchPointDescriptor> res = new LinkedList<TouchPointDescriptor>();
    for (List<TouchPointDescriptor> tpd : line2touchPoints.values()) {
      for (TouchPointDescriptor t : tpd) {
        if (tpd instanceof LineTouchPointDescriptor) {
          res.add(t);
        }
      }
      for (TouchPointDescriptor t : tpd) {
        if (!(tpd instanceof LineTouchPointDescriptor)) {
          res.add(t);
        }
      }
    }
    return res;
  }
}
