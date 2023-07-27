class PlaceHold {
  public int getNumberOfCoveredLines() {
    int number = 0;
    lock.lock();
    try {
      Iterator<CoverageData> iter = this.children.values().iterator();
      while (iter.hasNext()) {
        CoverageData coverageContainer = ((CoverageData) (iter.next()));
        number += coverageContainer.getNumberOfCoveredLines();
      }
    } finally {
      lock.unlock();
    }
    return number;
  }
}
