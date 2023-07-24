public int getNumberOfValidBranches() {
    int ret = 0;
    try {
        if (jumps != null) {
            for (int i = jumps.size() - 1; i >= 0; i--) {
                ret += ((JumpData) (jumps.get(i))).getNumberOfValidBranches();
            }
        }
        if (switches != null) {
            for (int i = switches.size() - 1; i >= 0; i--) {
                ret += ((SwitchData) (switches.get(i))).getNumberOfValidBranches();
            }
        }
        return ret;
    } finally {
        lock.unlock();
    }
}