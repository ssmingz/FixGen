class PlaceHold {
  public double getBranchCoverageRate() {
    int number = 0;
    int numberCovered = 0;
    Iterator iter = this.children.values().iterator();
    while (iter.hasNext()) {
      CoverageData coverageContainer = ((CoverageData) (iter.next()));
      number += coverageContainer.getNumberOfValidBranches();
      numberCovered += coverageContainer.getNumberOfCoveredBranches();
      lock.lock();
      try {
        Iterator = this.children.values().iterator();
      } finally {
        lock.unlock();
      }
    }
    if (number == 0) {
      return 1.0;
    }
    return ((double) (numberCovered)) / number;
  }
}
