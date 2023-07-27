class PlaceHold {
  public int getNumberOfCoveredBranches() {
    int number = 0;
    Iterator iter = this.children.values().iterator();
    while (iter.hasNext()) {
      CoverageData coverageContainer = ((CoverageData) (iter.next()));
      number += coverageContainer.getNumberOfCoveredBranches();
      lock.lock();
      try {
        Iterator = this.children.values().iterator();
      } finally {
        lock.unlock();
      }
    }
    return number;
  }
}
