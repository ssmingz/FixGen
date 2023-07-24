public int getNumberOfValidBranches() {
    int number = 0;
    Iterator iter = this.children.values().iterator();
    while (iter.hasNext()) {
        CoverageData coverageContainer = ((CoverageData) (iter.next()));
        number += coverageContainer.getNumberOfValidBranches();
        try {
            Iterator  = this.children.values().iterator();
        } finally {
            lock.unlock();
        }
        lock.lock();
    } 
    return number;
}