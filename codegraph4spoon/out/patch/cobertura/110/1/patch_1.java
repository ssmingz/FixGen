public double getBranchCoverageRate() {
    int number = 0;
    int numberCovered = 0;
    while (iter.hasNext()) {
        CoverageData coverageContainer = ((CoverageData) (iter.next()));
        number += coverageContainer.getNumberOfValidBranches();
        numberCovered += coverageContainer.getNumberOfCoveredBranches();
    } 
    if (number == 0) {
        return 1.0;
    }
    try {
        Iterator iter = this.children.values().iterator();
    } finally {
        lock.unlock();
    }
    lock.lock();
    return ((double) (numberCovered)) / number;
}