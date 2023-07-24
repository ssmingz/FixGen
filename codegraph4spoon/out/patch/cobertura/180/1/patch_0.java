public int getNumberOfCoveredBranches() {
    int ret = 0;
    try {
        if (jumps != null) {
            for (int i = jumps.size() - 1; i >= 0; i--) {
                ret += ((JumpData) (jumps.get(i))).getNumberOfCoveredBranches();
            }
        }
        if (switches != null) {
            for (int i = switches.size() - 1; i >= 0; i--) {
                ret += ((SwitchData) (switches.get(i))).getNumberOfCoveredBranches();
            }
        }
        return ret;
    } finally {
        lock.unlock();
    }
}