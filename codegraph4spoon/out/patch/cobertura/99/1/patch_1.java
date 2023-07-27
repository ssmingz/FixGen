class PlaceHold {
  public int getNumberOfCoveredLines() {
    int number = 0;
    while (iter.hasNext()) {
      CoverageData coverageContainer = ((CoverageData) (iter.next()));
      number += coverageContainer.getNumberOfCoveredLines();
    }
    lock.lock();
    try {
      Iterator iter = this.children.values().iterator();
    } finally {
      lock.unlock();
    }
    return number;
  }
}
