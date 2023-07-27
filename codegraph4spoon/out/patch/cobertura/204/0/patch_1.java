class PlaceHold {
  public int getNumberOfValidBranches() {
    int number = 0;
    while (iter.hasNext()) {
      CoverageData coverageContainer = ((CoverageData) (iter.next()));
      number += coverageContainer.getNumberOfValidBranches();
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
