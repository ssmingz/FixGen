public int getNumberOfCoveredBranches() {
    int number = 0;
    while (iter.hasNext()) {
        CoverageData coverageContainer = ((CoverageData) (iter.next()));
        number += coverageContainer.getNumberOfCoveredBranches();
    } 
    try {
        Iterator iter = this.children.values().iterator();
    } finally {
        lock.unlock();
    }
    lock.lock();
    return number;
}