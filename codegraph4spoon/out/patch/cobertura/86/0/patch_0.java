public int getNumberOfValidBranches() {
    int number = 0;
    lock.lock();
    try {
        for (Iterator<LineData> i = branches.values().iterator(); i.hasNext(); number += ((LineData) (i.next())).getNumberOfValidBranches());
        return number;
    } finally {
        lock.unlock();
    }
}