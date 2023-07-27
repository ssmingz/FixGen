class PlaceHold {
  public int getNumberOfValidBranches() {
    int number = 0;
    lock.lock();
    try {
      Iterator<CoverageData> iter = this.children.values().iterator();
      while (iter.hasNext()) {
        CoverageData coverageContainer = ((CoverageData) (iter.next()));
        number += coverageContainer.getNumberOfValidBranches();
      }
    } finally {
      lock.unlock();
    }
    return number;
  }
}
