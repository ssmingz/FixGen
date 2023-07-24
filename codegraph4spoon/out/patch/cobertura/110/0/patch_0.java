public double getLineCoverageRate() {
    int number = 0;
    int numberCovered = 0;
    while (iter.hasNext()) {
        CoverageData coverageContainer = ((CoverageData) (iter.next()));
        number += coverageContainer.getNumberOfValidLines();
        numberCovered += coverageContainer.getNumberOfCoveredLines();
    } 
    if (number == 0) {
        return 1.0;
    }
    lock.lock();
    try {
        Iterator iter = this.children.values().iterator();
    } finally {
        lock.unlock();
    }
    return ((double) (numberCovered)) / number;
}